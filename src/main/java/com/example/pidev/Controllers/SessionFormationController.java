package com.example.pidev.Controllers;
import com.example.pidev.Entities.SessionFormation;
import com.example.pidev.Services.SessionFormationService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@AllArgsConstructor
@NoArgsConstructor
@RequestMapping("/api/sessionformations")
public class SessionFormationController {
    @Autowired
    private SessionFormationService sessionFormationService;

    @PostMapping
    public ResponseEntity<SessionFormation> creerSessionFormation(@RequestBody SessionFormation sessionFormation) {
        SessionFormation nouvelleSession = sessionFormationService.creerSessionFormation(sessionFormation);
        return new ResponseEntity<>(nouvelleSession, HttpStatus.CREATED);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFormation(@PathVariable Long id) {
        sessionFormationService.deleteFormation(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/all")
    public ResponseEntity<List<SessionFormation>> getAllFormation() {
        List<SessionFormation> demandes = sessionFormationService.getAllFormations();
        return ResponseEntity.ok(demandes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SessionFormation> obtenirSessionFormationParId(@PathVariable Long id) {
        SessionFormation sessionFormation = sessionFormationService.obtenirSessionFormationParId(id);
        return new ResponseEntity<>(sessionFormation, HttpStatus.OK);
    }

    @GetMapping("/with-participants")
    public ResponseEntity<List<SessionFormation>> getAllSessionsWithParticipants() {
        List<SessionFormation> sessionsWithParticipants = sessionFormationService.getAllSessionsWithParticipants();
        return ResponseEntity.ok(sessionsWithParticipants);
    }

    @PostMapping("/{sessionId}/inscrire/{userId}")
    public ResponseEntity<SessionFormation> inscrireEmployeASession(@PathVariable Long sessionId, @PathVariable Long userId) {
        SessionFormation sessionMiseAJour = sessionFormationService.inscrireEmployeASession(sessionId, userId);
        return new ResponseEntity<>(sessionMiseAJour, HttpStatus.OK);
    }

    @GetMapping("/nombreFormations/{employeId}")
    public ResponseEntity<?> obtenirNombreFormationsParticipees(@PathVariable Long employeId) {
        try {
            int nombreFormations = sessionFormationService.compterFormationsParticipeesParEmploye(employeId);
            return ResponseEntity.ok(nombreFormations);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/nombreFormationdParticipesForUser/{userId}")
    public ResponseEntity<?> obtenirNombreFormationsParticipesForUser(@PathVariable Long userId) {
        try {
            int nombreEvenements = sessionFormationService.compterFormationsParticipesParUser(userId);
            return ResponseEntity.ok(nombreEvenements);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
