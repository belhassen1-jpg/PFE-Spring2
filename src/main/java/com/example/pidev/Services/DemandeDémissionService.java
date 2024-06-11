package com.example.pidev.Services;

import com.example.pidev.Entities.DemandeDémission;
import com.example.pidev.Entities.Employe;
import com.example.pidev.Entities.StatutDemande;
import com.example.pidev.Repositories.DemandeDémissionRepository;
import com.example.pidev.Repositories.EmployeRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class DemandeDémissionService {
    @Autowired
    private DemandeDémissionRepository demandeDémissionRepository;

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private GestionConventionService gestionConventionService;

    @Autowired
    private GestionEvenementService gestionEvenementService;

    public DemandeDémission creerDemandeDémission(DemandeDémission nouvelleDemande, Long userID) {
        if (nouvelleDemande == null) {
            throw new IllegalArgumentException("La demande de démission ne peut pas être nulle.");
        }

        Employe employe = employeRepository.findEmployeByUser_IdUser(userID)
                .orElseThrow(() -> new EntityNotFoundException("Employé non trouvé avec l'id : " ));

        nouvelleDemande.setEmploye(employe);
        nouvelleDemande.setDateDemande(new Date());
        nouvelleDemande.setStatut(StatutDemande.EN_ATTENTE);
        return demandeDémissionRepository.save(nouvelleDemande);
    }


    public DemandeDémission traiterDemandeDémission(Long demandeId, boolean accepter) {
        DemandeDémission demande = demandeDémissionRepository.findById(demandeId)
                .orElseThrow(() -> new EntityNotFoundException("Demande de démission non trouvée"));

        if (accepter) {
            demande.setStatut(StatutDemande.ACCEPTEE);
            Employe employe = demande.getEmploye();
            employe.setEndDate(new Date());
            employeRepository.save(employe);
            gestionConventionService.retirerEmployeDesConventions(employe.getEmpId());
            gestionEvenementService.retirerEmployeDesEvenements(employe.getEmpId());
        } else {
            demande.setStatut(StatutDemande.REFUSEE);
        }
        return demandeDémissionRepository.save(demande);
    }

    public List<DemandeDémission> getAllDemandeDemissions() {
        return demandeDémissionRepository.findAll();
    }

}
