package com.example.pidev.Controllers;
import com.example.pidev.Entities.DemandeConge;
import com.example.pidev.Entities.StatutDemande;
import com.example.pidev.Services.DemandeCongeService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@AllArgsConstructor
@NoArgsConstructor
@RequestMapping("/api/demandes-conge")
public class DemandeCongeController {
    @Autowired
    private DemandeCongeService demandeCongeService;

    @GetMapping
    public List<DemandeConge> getAllDemandesConge() {
        return demandeCongeService.getAllDemandesConge();
    }


    @GetMapping("/{id}")
    public DemandeConge getDemandeConge(@PathVariable Long id) {
        return demandeCongeService.getDemandeConge(id);
    }

    @DeleteMapping("/{id}")
    public void deleteDemandeConge(@PathVariable Long id) {
        demandeCongeService.deleteDemandeConge(id);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<DemandeConge> creerDemandeConge(@RequestBody DemandeConge demande, @PathVariable Long userId) {
        DemandeConge nouvelleDemande = demandeCongeService.creerDemandeConge(demande, userId);
        return new ResponseEntity<>(nouvelleDemande, HttpStatus.CREATED);
    }
    @PutMapping("/{id}/approuver/{empId}")
    public ResponseEntity<DemandeConge> approuverDemandeConge(
            @PathVariable Long id,
            @PathVariable Long empId,
            @RequestParam StatutDemande statut) {
        DemandeConge demandeApprouvee = demandeCongeService.approuverDemandeConge(id, empId, statut);
        return new ResponseEntity<>(demandeApprouvee, HttpStatus.OK);
    }


    @GetMapping("/{employeId}/total-jours-conges")
    public ResponseEntity<Long> calculerTotalJoursCongesPourEmploye(@PathVariable Long employeId) {
        Long totalJours = demandeCongeService.calculerTotalJoursCongesPourEmploye(employeId);
        return new ResponseEntity<>(totalJours, HttpStatus.OK);
    }

}
