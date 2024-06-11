package com.example.pidev.Controllers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.pidev.Entities.Convention;
import com.example.pidev.Entities.DemandeParticipationConvention;
import com.example.pidev.Services.GestionConventionService;
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
@RequestMapping("/api/conventions")
public class GestionConventionController {
@Autowired
    private GestionConventionService service;
    private static final Logger logger = LoggerFactory.getLogger(GestionConventionController.class);

    @PostMapping("/add")
    public ResponseEntity<Convention> addConvention(@RequestBody Convention convention, @RequestParam Long partenaireId) {
        Convention newConvention = service.saveOrUpdateConvention(convention, partenaireId);
        return new ResponseEntity<>(newConvention, HttpStatus.CREATED);
    }

    @PostMapping("/demandeParticipation")
    public ResponseEntity<DemandeParticipationConvention> creerDemandeParticipation(
            @RequestParam Long userId,
            @RequestParam Long conventionId) {

            DemandeParticipationConvention demande = service.creerDemandeParticipation(userId, conventionId);
            return new ResponseEntity<>(demande, HttpStatus.CREATED);
    }

    @PutMapping("/validerDemande/{demandeId}")
    public ResponseEntity<DemandeParticipationConvention> validerDemandeParticipation(@PathVariable Long demandeId) {
        DemandeParticipationConvention demande = service.validerDemandeParticipation(demandeId);
        return new ResponseEntity<>(demande, HttpStatus.OK);
    }

    @GetMapping("/parPartenaire/{partenaireId}")
    public List<Convention> trouverConventionsParPartenaire(@PathVariable Long partenaireId) {
        return service.trouverConventionsParPartenaire(partenaireId);
    }

    @GetMapping("/{conventionId}/employes")
    public List<DemandeParticipationConvention> trouverEmployesParConvention(@PathVariable Long conventionId) {
        return service.trouverEmployesParConvention(conventionId);
    }

    @GetMapping("/etParticipants")
    public ResponseEntity<List<Convention>> listerConventionsAvecParticipants() {
        List<Convention> conventions = service.trouverConventionsEtParticipants();
        return new ResponseEntity<>(conventions, HttpStatus.OK);
    }

    @GetMapping("/demandeConvention")
    public ResponseEntity<List<DemandeParticipationConvention>> getDemandeParticipationConvention() {
        List<DemandeParticipationConvention> demandes = service.getDemandeConvention();
        return new ResponseEntity<>(demandes, HttpStatus.OK);
    }



    @DeleteMapping("/employee/{empId}")
    public ResponseEntity<?> retirerEmployeDesConventions(@PathVariable Long empId) {
        service.retirerEmployeDesConventions(empId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/convention/{conventionId}/employee/{empId}")
    public ResponseEntity<?> retirerEmployeDeLConvention(@PathVariable Long conventionId, @PathVariable Long empId) {
        service.retirerEmployeDeLConvention(empId, conventionId);
        return ResponseEntity.ok().build();
    }


}