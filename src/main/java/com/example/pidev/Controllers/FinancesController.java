package com.example.pidev.Controllers;
import com.example.pidev.Entities.AnalyseFinanciere;
import com.example.pidev.Entities.Depense;
import com.example.pidev.Entities.ObjectifEpargne;
import com.example.pidev.Services.ServiceGestionFinanciere;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@AllArgsConstructor
@NoArgsConstructor
@RequestMapping("/api/finances")
public class FinancesController {

    @Autowired
    private ServiceGestionFinanciere serviceGestionFinanciere;

    @PostMapping("/depenses")
    public ResponseEntity<Depense> ajouterDepense(@RequestParam Long userId, @RequestBody Depense depense) {
        Depense nouvelleDepense = serviceGestionFinanciere.enregistrerDepense(userId, depense);
        return ResponseEntity.ok(nouvelleDepense);
    }

    @PostMapping("/objectifs-epargne")
    public ResponseEntity<ObjectifEpargne> ajouterObjectifEpargne(@RequestParam Long userId, @RequestBody ObjectifEpargne objectif) {
        ObjectifEpargne nouvelObjectif = serviceGestionFinanciere.definirObjectifEpargne(userId, objectif);
        return ResponseEntity.ok(nouvelObjectif);
    }

    @GetMapping("/analyses/{userId}")
    public ResponseEntity<AnalyseFinanciere> obtenirAnalyseFinanciere(@PathVariable Long userId) {
        AnalyseFinanciere analyse = serviceGestionFinanciere.analyserFinances(userId);
        return ResponseEntity.ok(analyse);
    }

    @GetMapping("/depenses/{userId}")
    public ResponseEntity<List<Depense>> listerDepenses(@PathVariable Long userId) {
        List<Depense> depenses = serviceGestionFinanciere.recupererDepenses(userId);
        return ResponseEntity.ok(depenses);
    }

    @GetMapping("/objectifs-epargne/{userId}")
    public ResponseEntity<List<ObjectifEpargne>> listerObjectifsEpargne(@PathVariable Long userId) {
        List<ObjectifEpargne> objectifs = serviceGestionFinanciere.recupererObjectifsEpargne(userId);
        return ResponseEntity.ok(objectifs);
    }

}
