package com.example.pidev.Services;

import com.example.pidev.Entities.*;
import com.example.pidev.Repositories.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class GestionEvenementService {
  @Autowired
    private EvenementRepository evenementRepository;
    @Autowired
    private PartenaireRepository partenaireRepository;
    @Autowired
    private DemandeParticipationEvenementRepository demandeParticipationEvenementRepository;
    @Autowired
    private EmployeRepository employeRepository;
    @Autowired
    private UserRepository userRepository;

    public Evenement creerOuMajEvenement(Evenement evenement, Long partenaireId) {
        Partenaire partenaire = partenaireRepository.findById(partenaireId)
                .orElseThrow(() -> new EntityNotFoundException("Partenaire non trouvé"));

        evenement.setPartenaire(partenaire);
        return evenementRepository.save(evenement);
    }


    public DemandeParticipationEvenement creerDemandeParticipation(Long userId, Long evenementId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        Employe employe = employeRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Employé non trouvé pour cet utilisateur"));
        Evenement evenement = evenementRepository.findById(evenementId)
                .orElseThrow(() -> new EntityNotFoundException("Événement non trouvé"));

        DemandeParticipationEvenement demande = new DemandeParticipationEvenement();
        demande.setEmploye(employe);
        demande.setEvenement(evenement);
        return demandeParticipationEvenementRepository.save(demande);
    }

    public DemandeParticipationEvenement validerDemandeParticipation(Long EventDemandeId) {
        DemandeParticipationEvenement demande = demandeParticipationEvenementRepository.findById(EventDemandeId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
        if (demande != null) {
            demande.setEstValidee(!demande.isEstValidee());
            return demandeParticipationEvenementRepository.save(demande);
        } else {
            throw new EntityNotFoundException("Demande non trouvée");
        }
    }


    public List<Evenement> trouverEvenementsParPartenaire(Long partenaireId) {
        return evenementRepository.findByPartenaireId(partenaireId);
    }

    public Set<Employe> trouverParticipantsParEvenement(Long evenementId) {
        List<DemandeParticipationEvenement> demandes = demandeParticipationEvenementRepository.findByEvenementIdAndEstValidee(evenementId, true);
        return demandes.stream().map(DemandeParticipationEvenement::getEmploye).collect(Collectors.toSet());
    }


    public List<Evenement> trouverEvenementsEtParticipants() {
        return evenementRepository.findAll();
    }

    public List<DemandeParticipationEvenement> getDemandeEvenement() {
        return demandeParticipationEvenementRepository.findAll();
    }

    public void retirerEmployeDesEvenements(Long empId) {
        List<DemandeParticipationEvenement> demandes = demandeParticipationEvenementRepository.findByEmployeIdAndEstValidee(empId, true);
        for (DemandeParticipationEvenement demande : demandes) {
            demande.setEstValidee(false);
        }
        demandeParticipationEvenementRepository.saveAll(demandes);
    }

    public void retirerEmployeDeLEvenement(Long empId, Long evenementId) {
        List<DemandeParticipationEvenement> demandes = demandeParticipationEvenementRepository.findByEmployeIdAndEvenementIdAndEstValidee(empId, evenementId, true);

        for (DemandeParticipationEvenement demande : demandes) {
            demande.setEstValidee(false);
        }
        demandeParticipationEvenementRepository.saveAll(demandes);
    }


    public int compterEvenementsParticipesParEmploye(Long employeId) {
        // Utilisation directe des demandes de participation pour compter les événements auxquels un employé a participé
        List<DemandeParticipationEvenement> demandesValidees = demandeParticipationEvenementRepository.findByEmployeIdAndEstValidee(employeId, true);

        return demandesValidees.size();
    }
    public int compterEvenementsParticipesParUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
        Employe employe = employeRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Employé non trouvé pour cet utilisateur"));
        // Utilisation directe des demandes de participation pour compter les événements auxquels un employé a participé
        List<DemandeParticipationEvenement> demandesValidees =
                demandeParticipationEvenementRepository.findByEmployeIdAndEstValidee(employe.getEmpId(), true);

        return demandesValidees.size();
    }

}
