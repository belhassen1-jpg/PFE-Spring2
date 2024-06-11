package com.example.pidev.Controllers;
import com.example.pidev.Entities.DemandeDémission;
import com.example.pidev.Services.DemandeDémissionService;
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
@RequestMapping("/api/demissions")
public class DemandeDémissionController {
    @Autowired
    private DemandeDémissionService demandeDémissionService;

    @PostMapping("/creer/{userID}")
    public ResponseEntity<?> creerDemandeDémission(@RequestBody DemandeDémission demande,@PathVariable Long userID) {
        if (demande == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Le corps de la requête ne peut pas être nul.");
        }

        try {
            DemandeDémission nouvelleDemande = demandeDémissionService.creerDemandeDémission(demande,userID);
            return ResponseEntity.status(HttpStatus.CREATED).body(nouvelleDemande);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @PostMapping("/traiter/{demandeId}")
    public ResponseEntity<DemandeDémission> traiterDemandeDémission(@PathVariable Long demandeId, @RequestParam boolean accepter) {
        DemandeDémission demande = demandeDémissionService.traiterDemandeDémission(demandeId, accepter);
        return new ResponseEntity<>(demande, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<DemandeDémission>> getAllDemissionRequests() {
        List<DemandeDémission> demandes = demandeDémissionService.getAllDemandeDemissions();
        return ResponseEntity.ok(demandes);
    }

}
