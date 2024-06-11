package com.example.pidev.Controllers;
import com.example.pidev.Entities.DemandeParticipationEvenement;
import com.example.pidev.Entities.Employe;
import com.example.pidev.Entities.Evenement;
import com.example.pidev.Services.GestionEvenementService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Set;

@RestController
@AllArgsConstructor
@NoArgsConstructor
@RequestMapping("/api/evenements")
public class EvenementController {

    @Autowired
    private GestionEvenementService gestionEvenementService;

    @PostMapping("/CreerEvenement")
    public ResponseEntity<Evenement> creerEvenement(
            @RequestBody Evenement evenementData,
            @RequestParam Long partenaireId) {
        Evenement evenementCree = gestionEvenementService.creerOuMajEvenement(evenementData, partenaireId);
        return new ResponseEntity<>(evenementCree, HttpStatus.CREATED);
    }

    @PostMapping("/demandeParticipation")
    public ResponseEntity<DemandeParticipationEvenement> creerDemandeParticipation(
            @RequestParam Long userId,
            @RequestParam Long evenementId) {
        DemandeParticipationEvenement demande = gestionEvenementService.creerDemandeParticipation(userId, evenementId);
        return new ResponseEntity<>(demande, HttpStatus.CREATED);
    }

    @PutMapping("/validerDemande/{EventDemandeId}")
    public ResponseEntity<DemandeParticipationEvenement> validerDemandeParticipation(@PathVariable Long EventDemandeId) {
        DemandeParticipationEvenement demande = gestionEvenementService.validerDemandeParticipation(EventDemandeId);
        return new ResponseEntity<>(demande, HttpStatus.OK);
    }

    @GetMapping("/parPartenaire/{partenaireId}")
    public ResponseEntity<List<Evenement>> trouverEvenementsParPartenaire(@PathVariable Long partenaireId) {
        List<Evenement> evenements = gestionEvenementService.trouverEvenementsParPartenaire(partenaireId);
        return new ResponseEntity<>(evenements, HttpStatus.OK);
    }

    @GetMapping("/{evenementId}/participants")
    public ResponseEntity<Set<Employe>> trouverParticipantsParEvenement(@PathVariable Long evenementId) {
        Set<Employe> participants = gestionEvenementService.trouverParticipantsParEvenement(evenementId);
        return new ResponseEntity<>(participants, HttpStatus.OK);
    }

    @GetMapping("/etParticipants")
    public ResponseEntity<List<Evenement>> listerEvenementsAvecParticipants() {
        List<Evenement> evenements = gestionEvenementService.trouverEvenementsEtParticipants();
        return new ResponseEntity<>(evenements, HttpStatus.OK);
    }

    @GetMapping("/demandeEvent")
    public ResponseEntity<List<DemandeParticipationEvenement>> getDemandeParticipationEvent() {
        List<DemandeParticipationEvenement> demandes = gestionEvenementService.getDemandeEvenement();
        return new ResponseEntity<>(demandes, HttpStatus.OK);
    }

    @DeleteMapping("/employee/{empId}")
    public ResponseEntity<?> retirerEmployeDesEvenements(@PathVariable Long empId) {
        gestionEvenementService.retirerEmployeDesEvenements(empId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/evenement/{evenementId}/employee/{empId}")
    public ResponseEntity<?> retirerEmployeDeLEvenement(@PathVariable Long evenementId, @PathVariable Long empId) {
        gestionEvenementService.retirerEmployeDeLEvenement(empId, evenementId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/nombreEvenementsParticipes/{employeId}")
    public ResponseEntity<?> obtenirNombreEvenementsParticipes(@PathVariable Long employeId) {
        try {
            int nombreEvenements = gestionEvenementService.compterEvenementsParticipesParEmploye(employeId);
            return ResponseEntity.ok(nombreEvenements);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/nombreEvenementsParticipesForUser/{userId}")
    public ResponseEntity<?> obtenirNombreEvenementsParticipesForUser(@PathVariable Long userId) {
        try {
            int nombreEvenements = gestionEvenementService.compterEvenementsParticipesParUser(userId);
            return ResponseEntity.ok(nombreEvenements);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
