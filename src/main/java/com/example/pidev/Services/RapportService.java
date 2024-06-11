package com.example.pidev.Services;
import com.example.pidev.Entities.*;
import com.example.pidev.Repositories.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.Optional;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import javax.persistence.EntityNotFoundException;


@Service
@AllArgsConstructor
@NoArgsConstructor
public class RapportService {
    @Autowired
    private RapportRepository rapportRepository;
    @Autowired
    private EmployeRepository employeRepository;
    @Autowired
    private PlanningService planningService;
    @Autowired
    private DemandeCongeService demandeCongeService;
    @Autowired
    private SessionFormationService sessionFormationService;
    @Autowired
    private GestionEvenementService gestionEvenementService;

    @Autowired
    private EvaluationPerformanceRepository evaluationPerformanceRepository;

    @Autowired
    private BulletinPaieRepository bulletinPaieRepository;

    @Autowired
    private UserRepository userRepository;
    public Rapport genererOuMettreAJourRapport(Long employeId) {
        Employe employe = employeRepository.findById(employeId)
                .orElseThrow(() -> new RuntimeException("Employé non trouvé avec l'id : " + employeId));

        Rapport rapport = rapportRepository.findByEmployeEmpId(employeId)
                .orElse(new Rapport());


        rapport.setEmploye(employe);
        rapport.setTotalHeuresTravaillees(planningService.calculerTotalHeuresTravailleesPourEmploye(employeId));
        rapport.setTotalJoursConges(demandeCongeService.calculerTotalJoursCongesPourEmploye(employeId));
        rapport.setNombreFormationsParticipees(sessionFormationService.compterFormationsParticipeesParEmploye(employeId));
        rapport.setNombreEvenementsParticipes(gestionEvenementService.compterEvenementsParticipesParEmploye(employeId));

        Optional<EvaluationPerformance> derniereEvaluation = evaluationPerformanceRepository.findTopByEmployeEmpIdOrderByAnneeDesc(employeId);
        if (derniereEvaluation.isPresent()) {
            rapport.setEvaluationPerformanceRecente(derniereEvaluation.get().getCommentaire());
        }

        Optional<BulletinPaie> dernierBulletinPaie = bulletinPaieRepository.findTopByEmployeIdOrderByDateEmissionDesc(employeId);
        if (dernierBulletinPaie.isPresent()) {
            rapport.setDernierSalaireBrut(dernierBulletinPaie.get().getSalaireBrut());
            rapport.setDernierSalaireNet(dernierBulletinPaie.get().getSalaireNet());
        }
        rapport.setDateRapport(new Date());
        return rapportRepository.save(rapport);
    }

    public Rapport genererOuMettreAJourRapportForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
        Employe employe = employeRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Employé non trouvé pour cet utilisateur"));

        Rapport rapport = rapportRepository.findByEmployeEmpId(employe.getEmpId())
                .orElse(new Rapport());


        rapport.setEmploye(employe);
        rapport.setTotalHeuresTravaillees(planningService.calculerTotalHeuresTravailleesPourEmploye(employe.getEmpId()));
        rapport.setTotalJoursConges(demandeCongeService.calculerTotalJoursCongesPourEmploye(employe.getEmpId()));
        rapport.setNombreFormationsParticipees(sessionFormationService.compterFormationsParticipeesParEmploye(employe.getEmpId()));
        rapport.setNombreEvenementsParticipes(gestionEvenementService.compterEvenementsParticipesParEmploye(employe.getEmpId()));

        Optional<EvaluationPerformance> derniereEvaluation = evaluationPerformanceRepository.findTopByEmployeEmpIdOrderByAnneeDesc(employe.getEmpId());
        if (derniereEvaluation.isPresent()) {
            rapport.setEvaluationPerformanceRecente(derniereEvaluation.get().getCommentaire());
        }

        Optional<BulletinPaie> dernierBulletinPaie = bulletinPaieRepository.findTopByEmployeIdOrderByDateEmissionDesc(employe.getEmpId());
        if (dernierBulletinPaie.isPresent()) {
            rapport.setDernierSalaireBrut(dernierBulletinPaie.get().getSalaireBrut());
            rapport.setDernierSalaireNet(dernierBulletinPaie.get().getSalaireNet());
        }
        rapport.setDateRapport(new Date());
        return rapportRepository.save(rapport);
    }

    public byte[] genererPdfRapport(Rapport rapport) throws DocumentException {
        Document document = new Document();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, byteArrayOutputStream);
        document.open();

        document.add(new Paragraph("Rapport pour l'employé: " + rapport.getEmploye().getFirstName() + " " + rapport.getEmploye().getLastName()));
        document.add(new Paragraph("Total des heures travaillées: " + rapport.getTotalHeuresTravaillees()));
        document.add(new Paragraph("Total des jours de congés: " + rapport.getTotalJoursConges()));
        document.add(new Paragraph("Nombre de formations participées: " + rapport.getNombreFormationsParticipees()));
        document.add(new Paragraph("Nombre d'événements participés: " + rapport.getNombreEvenementsParticipes()));
        document.add(new Paragraph("Évaluation de performance récente: " + rapport.getEvaluationPerformanceRecente()));
        document.add(new Paragraph("Dernier salaire brut: " + rapport.getDernierSalaireBrut()));
        document.add(new Paragraph("Dernier salaire net: " + rapport.getDernierSalaireNet()));

        document.close();
        return byteArrayOutputStream.toByteArray();
    }

}