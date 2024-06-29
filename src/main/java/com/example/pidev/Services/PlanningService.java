package com.example.pidev.Services;

import com.example.pidev.Entities.Employe;
import com.example.pidev.Entities.FeuilleTemps;
import com.example.pidev.Entities.Planning;
import com.example.pidev.Repositories.EmployeRepository;
import com.example.pidev.Repositories.FeuilleTempsRepository;
import com.example.pidev.Repositories.PlanningRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class PlanningService {
    @Autowired
    private PlanningRepository planningRepository;
    @Autowired
    private FeuilleTempsRepository feuilleTempsRepository;
    @Autowired
    private EmployeRepository employeRepository;

    public Planning creerEtAffecterPlanning(Planning planning, List<Long> employeIds) {
        Set<Employe> employes = employeIds.stream()
                .map(id -> employeRepository.findById(id).orElseThrow(() ->
                        new EntityNotFoundException("Employé not found with id: " + id)))
                .collect(Collectors.toSet());

        planning.setEmployesAffectes(employes);
        return planningRepository.save(planning);
    }

    public List<Planning> findPlanningsByEmployeeId(Long employeeId) {
        return planningRepository.findByEmployeeId(employeeId);
    }


    public FeuilleTemps creerEtAssocierFeuilleTemps(Long planningId, Long userId, FeuilleTemps feuilleTemps) {
        Planning planning = planningRepository.findById(planningId)
                .orElseThrow(() -> new EntityNotFoundException("Planning not found with id: " + planningId));
        Employe employe = employeRepository.findEmployeByUser_IdUser(userId)
                .orElseThrow(() -> new RuntimeException("Employé non trouvé"));

        // Vérifiez si l'employé est affecté au planning avant de permettre la création de la feuille de temps
        if (!planning.getEmployesAffectes().contains(employe)) {
            throw new IllegalArgumentException("Employe is not assigned to this planning");
        }

        feuilleTemps.setPlanning(planning);
        return feuilleTempsRepository.save(feuilleTemps);
    }

    public List<Planning> obtenirTousLesPlanningsAvecDetails() {
        return planningRepository.findAllWithDetails();
    }


    public Planning obtenirPlanningParNomProjet(String nomProjet) {
        return planningRepository.findByNomProjet(nomProjet)
                .orElseThrow(() -> new EntityNotFoundException("Planning non trouvé avec le nom de projet : " + nomProjet));
    }






    public List<FeuilleTemps> obtenirFeuillesDeTemps(Long planningId, Long employeId) {
        if(planningId != null){
            return feuilleTempsRepository.findByPlanningId(planningId);
        } else if(employeId != null){
            return feuilleTempsRepository.findByEmployeId(employeId);
        } else {
            throw new IllegalArgumentException("Au moins un des paramètres (planningId ou employeId) est requis.");
        }
    }

    public List<FeuilleTemps> obtenirFeuillesDeTempsForUser(Long planningId, Long userId) {
        Employe employe = employeRepository.findEmployeByUser_IdUser(userId)
                .orElseThrow(() -> new RuntimeException("Employé non trouvé"));
        if(planningId != null){
            return feuilleTempsRepository.findByPlanningId(planningId);
        } else if(employe.getEmpId() != null){
            return feuilleTempsRepository.findByEmployeId(employe.getEmpId());
        } else {
            throw new IllegalArgumentException("Au moins un des paramètres (planningId ou employeId) est requis.");
        }
    }

    public FeuilleTemps changerApprobationFeuilleTemps(Long feuilleTempsId) {
        FeuilleTemps feuilleTemps = feuilleTempsRepository.findById(feuilleTempsId)
                .orElseThrow(() -> new EntityNotFoundException("Feuille de temps non trouvée"));
        feuilleTemps.setEstApprouve(!feuilleTemps.isEstApprouve());
        return feuilleTempsRepository.save(feuilleTemps);
    }

    public Long calculerHeuresTravaillees(FeuilleTemps feuilleTemps) {
        long heuresTravaillees = 0;
        if (feuilleTemps.getHeureDebut() != null && feuilleTemps.getHeureFin() != null) {
            long diff = feuilleTemps.getHeureFin().getTime() - feuilleTemps.getHeureDebut().getTime();
            heuresTravaillees = diff / (60 * 60 * 1000);
        }
        return heuresTravaillees;
    }

    public Long calculerTotalHeuresTravailleesPourEmploye(Long employeId) {
        List<FeuilleTemps> feuilles = feuilleTempsRepository.findByEmployeId(employeId);
        long totalHeures = 0;
        for (FeuilleTemps feuille : feuilles) {
            if (feuille.isEstApprouve()) {
                totalHeures += calculerHeuresTravaillees(feuille);
            }
        }
        return totalHeures;
    }

    public List<FeuilleTemps> obtenirFeuillesDeTempsPourPeriode(Long employeId, Date dateDebut, Date dateFin) {
        if (employeId == null || dateDebut == null || dateFin == null) {
            throw new IllegalArgumentException("L'ID de l'employé et les dates de début et de fin sont requis.");
        }

        List<FeuilleTemps> feuilles = feuilleTempsRepository.findByEmployeIdAndPeriode(employeId, dateDebut, dateFin);
        if (feuilles.isEmpty()) {
            throw new EntityNotFoundException("Aucune feuille de temps trouvée pour l'employé avec l'ID " + employeId
                    + " pour la période spécifiée.");
        }

        return feuilles;
    }

    public List<Map<String, Object>> getEmployeeRankingByHours(String nomProjet) {
        Planning planning = planningRepository.findByNomProjet(nomProjet)
                .orElseThrow(() -> new EntityNotFoundException("Planning not found with project name: " + nomProjet));
        List<FeuilleTemps> feuilles = feuilleTempsRepository.findByPlanningId(planning.getId());
        Set<Employe> employees = planning.getEmployesAffectes();
        Map<Employe, Long> employeeHours = new HashMap<>();
        for (FeuilleTemps feuille : feuilles) {
            for (Employe employe : employees) {
                long hours = calculerHeuresTravaillees(feuille);
                employeeHours.put(employe, employeeHours.getOrDefault(employe, 0L) + hours);
            }
        }


        return employeeHours.entrySet().stream()
                .sorted(Map.Entry.<Employe, Long>comparingByValue().reversed())
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("employeId", entry.getKey().getEmpId());
                    map.put("nom", entry.getKey().getFirstName() + " " + entry.getKey().getLastName());
                    map.put("heuresTravaillees", entry.getValue());
                    return map;
                })
                .collect(Collectors.toList());
    }

}