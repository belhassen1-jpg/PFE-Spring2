package com.example.pidev.Services;

import com.example.pidev.Entities.*;
import com.example.pidev.Repositories.BulletinPaieRepository;
import com.example.pidev.Repositories.DeclarationFiscaleRepository;
import com.example.pidev.Repositories.EmployeRepository;
import com.example.pidev.Repositories.PaieRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class ServicePaie {
    @Autowired
    private EmployeRepository employeRepository;
    @Autowired
    private PaieRepository paieRepository;
    @Autowired
    private BulletinPaieRepository bulletinPaieRepository;
    @Autowired
    private DeclarationFiscaleRepository declarationFiscaleRepository;
    @Autowired
    private PlanningService planningService;




    @Transactional
    public Paie calculerPaie(Long employeId) {

        Employe employe = employeRepository.findById(employeId)
                .orElseThrow(() -> new RuntimeException("Employé non trouvé avec l'id : " + employeId));


        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        Date dateDebut = cal.getTime();

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date dateFin = cal.getTime();
        List<FeuilleTemps> feuillesDeTemps = planningService.obtenirFeuillesDeTempsPourPeriode(employeId, dateDebut, dateFin);


        BigDecimal totalHeuresTravaillees = BigDecimal.ZERO;
        BigDecimal totalHeuresSup = BigDecimal.ZERO;
        BigDecimal seuilHeuresNormales = BigDecimal.valueOf(4);

        for (FeuilleTemps feuille : feuillesDeTemps) {
            if (feuille.isEstApprouve()) {
                long diff = feuille.getHeureFin().getTime() - feuille.getHeureDebut().getTime();
                BigDecimal heuresTravaillees = BigDecimal.valueOf(diff / (60 * 60 * 1000));
                totalHeuresTravaillees = totalHeuresTravaillees.add(heuresTravaillees);

                if (heuresTravaillees.compareTo(seuilHeuresNormales) > 0) {
                    BigDecimal heuresSup = heuresTravaillees.subtract(seuilHeuresNormales);
                    totalHeuresSup = totalHeuresSup.add(heuresSup);
                }
            }
        }

        Paie paie = new Paie();
        paie.setEmploye(employe);
        paie.setDatePaie(new Date());
        paie.setHeuresTravaillees(totalHeuresTravaillees);
        paie.setHeuresSupplementaires(totalHeuresSup);
        paie.setTauxHoraire(employe.getTauxHoraire());
        paie.setTauxHeuresSupplementaires(employe.getTauxHeuresSupplementaires());
        paie.setMontantPrimes(employe.getMontantPrimes());
        paie.setMontantDeductions(employe.getMontantDeductions());


            BigDecimal salaireBrut = employe.getTauxHoraire().multiply(totalHeuresTravaillees);
            salaireBrut = salaireBrut.add(employe.getTauxHeuresSupplementaires().multiply(totalHeuresSup));


            BigDecimal montantPrimes = employe.getMontantPrimes();
            BigDecimal montantDeductions = employe.getMontantDeductions();
            salaireBrut = salaireBrut.add(montantPrimes).subtract(montantDeductions);




            BigDecimal tauxCotisationsSociales = new BigDecimal("0.0918"); // 9,18% total, à ajuster selon les taux actuels
            BigDecimal cotisationsSociales = salaireBrut.multiply(tauxCotisationsSociales);
            paie.setCotisationsSociales(cotisationsSociales);

            BigDecimal impotSurRevenu = BigDecimal.ZERO;

            if (salaireBrut.compareTo(new BigDecimal("200")) <= 0) {
                impotSurRevenu = BigDecimal.ZERO;
            } else if (salaireBrut.compareTo(new BigDecimal("200")) > 0 && salaireBrut.compareTo(new BigDecimal("800")) <= 0) {
                impotSurRevenu = (salaireBrut.subtract(new BigDecimal("200"))).multiply(new BigDecimal("0.26"));
            } else {
                impotSurRevenu = (salaireBrut.subtract(new BigDecimal("800"))).multiply(new BigDecimal("0.28"))
                        .add(new BigDecimal("800").subtract(new BigDecimal("200")).multiply(new BigDecimal("0.26")));
            }

            paie.setImpotSurRevenu(impotSurRevenu);


            BigDecimal salaireNet = salaireBrut.subtract(cotisationsSociales).subtract(impotSurRevenu);
            paie.setSalaireNet(salaireNet);
            paie.setSalaireBrut(salaireBrut);

        return paieRepository.save(paie);
        }





    @Transactional
    public BulletinPaie genererBulletinPaie(Long employeId) {

        Paie dernierePaie = paieRepository.findTopByEmployeEmpIdOrderByDatePaieDesc(employeId)
                .orElseThrow(() -> new RuntimeException("Dernière paie non trouvée pour l'employé avec l'id : " + employeId));

        BulletinPaie bulletin = new BulletinPaie();
        bulletin.setDatePaie(dernierePaie.getDatePaie());
        bulletin.setEmploye(dernierePaie.getEmploye());
        bulletin.setTauxHoraire(dernierePaie.getTauxHoraire());
        bulletin.setHeuresTravaillees(dernierePaie.getHeuresTravaillees());
        bulletin.setHeuresSupplementaires(dernierePaie.getHeuresSupplementaires());
        bulletin.setTauxHeuresSupplementaires(dernierePaie.getTauxHeuresSupplementaires());
        bulletin.setMontantPrimes(dernierePaie.getMontantPrimes());
        bulletin.setMontantDeductions(dernierePaie.getMontantDeductions());
        bulletin.setCotisationsSociales(dernierePaie.getCotisationsSociales());
        bulletin.setImpotSurRevenu(dernierePaie.getImpotSurRevenu());
        bulletin.setSalaireNet(dernierePaie.getSalaireNet());
        bulletin.setSalaireBrut(dernierePaie.getSalaireBrut());


        bulletin.setDateEmission(new Date());

        Calendar cal = Calendar.getInstance();
        cal.setTime(dernierePaie.getDatePaie());
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        Date periodeDebut = cal.getTime();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date periodeFin = cal.getTime();
        bulletin.setPeriodeDebut(periodeDebut);
        bulletin.setPeriodeFin(periodeFin);
        bulletin.setCommentaire("Bulletin généré automatiquement pour la période indiquée ");
        bulletin.setNomEntreprise(" Sascode ");
        bulletin.setAdresseEntreprise("IMM, Les Arcades, Rue Lac Loch Ness, Tunis 1053 Les berges du lac 1, Tunis 1053");


        return bulletinPaieRepository.save(bulletin);
    }




    @Transactional
    public DeclarationFiscale genererDeclarationFiscale(Employe employe) {
        if (employe == null) {
            throw new IllegalArgumentException("L'objet Employe ne peut pas être null.");
        }


        BulletinPaie dernierBulletinPaie = bulletinPaieRepository.findTopByEmployeIdOrderByDateEmissionDesc(employe.getEmpId())
                .orElseThrow(() -> new RuntimeException("Dernier bulletin de paie non trouvé pour l'employé : " + employe.getEmpId()));


        DeclarationFiscale declaration = new DeclarationFiscale();

        declaration.setBulletinPaie(dernierBulletinPaie);
        declaration.setDateDeclaration(new Date());


        declaration.setTotalRevenuImposable(dernierBulletinPaie.getSalaireBrut());
        declaration.setMontantImpotDu(dernierBulletinPaie.getImpotSurRevenu());
        declaration.setMontantCotisationsSocialesDu(dernierBulletinPaie.getCotisationsSociales());


        String referenceDeclaration = "DF-" + employe.getEmpId() + "-" + System.currentTimeMillis();
        declaration.setReferenceDeclaration(referenceDeclaration);


        declaration.setAutoriteFiscale("Direction Générale des Impôts de Tunisie");


        return declarationFiscaleRepository.save(declaration);
    }





    public List<BulletinPaie> obtenirBulletinsPaie(Long employeId) {
        return bulletinPaieRepository.findByEmployeEmpId(employeId);
    }

    public List<BulletinPaie> obtenirBulletinsPaieForUser(Long userId) {
        Employe employe = employeRepository.findEmployeByUser_IdUser(userId)
                .orElseThrow(() -> new RuntimeException("Employé non trouvé"));
        return bulletinPaieRepository.findByEmployeEmpId(employe.getEmpId());
    }

    public BulletinPaie obtenirDernierBulletinPaie(Long employeId) {
        return bulletinPaieRepository.findTopByEmployeIdOrderByDateEmissionDesc(employeId).orElse(null);
    }

    public BulletinPaie obtenirDernierBulletinPaieForUser(Long userId) {
        Employe employe = employeRepository.findEmployeByUser_IdUser(userId)
                .orElseThrow(() -> new RuntimeException("Employé non trouvé"));
        return bulletinPaieRepository.findTopByEmployeIdOrderByDateEmissionDesc(employe.getEmpId()).orElse(null);
    }


    public Paie obtenirDernierePaie(Long employeId) {
        return paieRepository.findTopByEmployeEmpIdOrderByDatePaieDesc(employeId).orElse(null);
    }

    public Paie obtenirDernierePaieForUser(Long userId) {
        Employe employe = employeRepository.findEmployeByUser_IdUser(userId)
                .orElseThrow(() -> new RuntimeException("Employé non trouvé"));
        return paieRepository.findTopByEmployeEmpIdOrderByDatePaieDesc(employe.getEmpId()).orElse(null);
    }

    public List<DeclarationFiscale> obtenirDeclarationsFiscales(Long employeId) {
        return declarationFiscaleRepository.findByEmployeEmpId(employeId);
    }


}


