package com.example.pidev.Services;

import com.example.pidev.Entities.*;
import com.example.pidev.Repositories.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class ServiceGestionFinanciere {

    @Autowired
    private DepenseRepository depenseRepository;

    @Autowired
    private ObjectifEpargneRepository objectifEpargneRepository;

    @Autowired
    private AnalyseFinanciereRepository analyseFinanciereRepository;

    @Autowired
    private EmployeRepository employeRepository;
    @Autowired
    private PaieRepository paieRepository;

    @Transactional
    public Depense enregistrerDepense(Long userId, Depense depense) {
        if (depense.getMontant() == null || depense.getMontant().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant de la dépense doit être supérieur à zéro.");
        }
        if (depense.getCategorie() == null || depense.getCategorie().isEmpty()) {
            throw new IllegalArgumentException("La catégorie de la dépense est obligatoire.");
        }
        if (depense.getDescription() == null || depense.getDescription().isEmpty()) {
            throw new IllegalArgumentException("La description de la dépense est obligatoire.");
        }

        Employe employe = employeRepository.findEmployeByUser_IdUser(userId)
                .orElseThrow(() -> new RuntimeException("Employé non trouvé"));

        depense.setEmploye(employe);
        Depense savedDepense = depenseRepository.save(depense);

        updateObjectifEpargneForEmploye(employe.getEmpId(), depense);

        return savedDepense;
    }
    private void updateObjectifEpargneForEmploye(Long employeId, Depense depense) {
        List<ObjectifEpargne> objectifs = objectifEpargneRepository.findByEmployeEmpId(employeId);
        objectifs.stream()
                .filter(obj -> !depense.getDateDepense().before(obj.getDateDebut()) && !depense.getDateDepense().after(obj.getDateFin()))
                .forEach(this::updateMontantActuel);
    }

    private void updateMontantActuel(ObjectifEpargne objectif) {
        BigDecimal salaireNet = paieRepository.findTopByEmployeEmpIdOrderByDatePaieDesc(objectif.getEmploye().getEmpId())
                .map(Paie::getSalaireNet)
                .orElse(BigDecimal.ZERO);
        BigDecimal totalDepenses = depenseRepository.findByEmployeEmpIdAndDateDepenseBetween(
                        objectif.getEmploye().getEmpId(), objectif.getDateDebut(), objectif.getDateFin())
                .stream()
                .map(Depense::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal montantActuel = salaireNet.subtract(totalDepenses);
        objectif.setMontantActuel(montantActuel.compareTo(BigDecimal.ZERO) > 0 ? montantActuel : BigDecimal.ZERO);
        objectifEpargneRepository.save(objectif);
    }

    public ObjectifEpargne definirObjectifEpargne(Long userId, ObjectifEpargne objectif) {
        if (objectif.getObjectifMontant() == null || objectif.getObjectifMontant().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant de l'objectif doit être supérieur à zéro.");
        }
        if (objectif.getDateDebut() == null) {
            throw new IllegalArgumentException("La date de début est obligatoire.");
        }
        if (objectif.getDateFin() == null || objectif.getDateFin().before(objectif.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin est obligatoire et doit être après la date de début.");
        }

        Employe employe = employeRepository.findEmployeByUser_IdUser(userId)
                .orElseThrow(() -> new RuntimeException("Employé non trouvé"));

        // Récupérer le dernier salaire net de l'employé
        BigDecimal salaireNet = paieRepository.findTopByEmployeEmpIdOrderByDatePaieDesc(employe.getEmpId())
                .map(Paie::getSalaireNet)
                .orElse(BigDecimal.ZERO);

        // Calculer la somme des dépenses de l'employé pour la période correspondante
        BigDecimal totalDepenses = depenseRepository.findByEmployeEmpIdAndDateDepenseBetween(
                        employe.getEmpId(),
                        objectif.getDateDebut(),
                        objectif.getDateFin())
                .stream()
                .map(Depense::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculer le montant actuel de l'objectif d'épargne
        BigDecimal montantActuel = salaireNet.subtract(totalDepenses);

        objectif.setEmploye(employe);
        objectif.setMontantActuel(montantActuel.compareTo(BigDecimal.ZERO) > 0 ? montantActuel : BigDecimal.ZERO);
        return objectifEpargneRepository.save(objectif);
    }

    public AnalyseFinanciere analyserFinances(Long userId) {
        Employe employe = employeRepository.findEmployeByUser_IdUser(userId)
                .orElseThrow(() -> new RuntimeException("Employé non trouvé"));

        AnalyseFinanciere analyse = new AnalyseFinanciere();
        analyse.setEmploye(employe);
        analyse.setDateAnalyse(new Date());

        BigDecimal depenseMoyenneMensuelle = calculerDepenseMoyenneMensuelle(employe.getEmpId());
        BigDecimal epargneMoyenneMensuelle = calculerEpargneMoyenneMensuelle(employe.getEmpId());
        BigDecimal tauxEpargneMensuel = depenseMoyenneMensuelle.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO :
                epargneMoyenneMensuelle.divide(depenseMoyenneMensuelle, MathContext.DECIMAL128);

        analyse.setDepenseMoyenneMensuelle(depenseMoyenneMensuelle);
        analyse.setEpargneMoyenneMensuelle(epargneMoyenneMensuelle);
        analyse.setTauxEpargneMensuel(tauxEpargneMensuel);


        if (tauxEpargneMensuel.compareTo(new BigDecimal("0.20")) >= 0) {
            analyse.setResume("Votre résumé financier mensuel montre une excellente capacité d'épargne.");
            analyse.setRecommandations("Vous pourriez envisager d'investir une partie de votre épargne pour optimiser votre patrimoine financier.");
        } else if (tauxEpargneMensuel.compareTo(new BigDecimal("0.10")) >= 0) {
            analyse.setResume("Votre résumé financier mensuel indique une bonne capacité d'épargne.");
            analyse.setRecommandations("Il pourrait être judicieux de revoir vos dépenses pour augmenter votre taux d'épargne.");
        } else {
            analyse.setResume("Votre résumé financier mensuel montre une faible capacité d'épargne.");
            analyse.setRecommandations("Nous recommandons de surveiller vos dépenses et d'essayer de réduire les coûts non essentiels.");
        }

        return analyseFinanciereRepository.save(analyse);
    }


    public List<Depense> recupererDepenses(Long userId) {
        Employe employe = employeRepository.findEmployeByUser_IdUser(userId)
                .orElseThrow(() -> new RuntimeException("Employé non trouvé"));
        return depenseRepository.findByEmployeEmpId(employe.getEmpId());
    }

    public List<ObjectifEpargne> recupererObjectifsEpargne(Long userId) {
        Employe employe = employeRepository.findEmployeByUser_IdUser(userId)
                .orElseThrow(() -> new RuntimeException("Employé non trouvé"));
        return objectifEpargneRepository.findByEmployeEmpId(employe.getEmpId());
    }

    public BigDecimal calculerDepenseMoyenneMensuelle(Long employeId) {
        List<Depense> depenses = recupererDepenses(employeId);
        BigDecimal totalDepenses = depenses.stream()
                .map(Depense::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalDepenses.divide(BigDecimal.valueOf(12), RoundingMode.HALF_UP);
    }

    public BigDecimal calculerEpargneMoyenneMensuelle(Long employeId) {
        List<ObjectifEpargne> epargnes = recupererObjectifsEpargne(employeId);
        BigDecimal totalEpargne = epargnes.stream()
                .map(ObjectifEpargne::getMontantActuel)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalEpargne.divide(BigDecimal.valueOf(12), RoundingMode.HALF_UP);
    }

}
