package com.example.pidev.Controllers;

import com.example.pidev.Entities.FeuilleTemps;
import com.example.pidev.Entities.Planning;
import com.example.pidev.Repositories.FeuilleTempsRepository;
import com.example.pidev.Services.PlanningService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@NoArgsConstructor
@RequestMapping("/api/plannings")
public class PlanningController {
    @Autowired
    private PlanningService planningService;


    @PostMapping("/createWithEmployees")
    public ResponseEntity<Planning> creerPlanningAvecEmployes(
            @RequestBody Planning planning,
            @RequestParam List<Long> employeIds) {
        Planning createdPlanning = planningService.creerEtAffecterPlanning(planning, employeIds);
        return new ResponseEntity<>(createdPlanning, HttpStatus.CREATED);
    }

    @GetMapping("/employee/{employeeId}/plannings")
    public ResponseEntity<List<Planning>> getEmployeePlannings(@PathVariable Long employeeId) {
        List<Planning> plannings = planningService.findPlanningsByEmployeeId(employeeId);
        if (plannings.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(plannings);
    }


    @PostMapping("/feuilleTemps/planning/{planningId}/employe/{userId}")
    public ResponseEntity<FeuilleTemps> creerEtAssocierFeuilleTemps(
            @PathVariable Long planningId,
            @PathVariable Long userId,
            @RequestBody FeuilleTemps feuilleTemps) {

        FeuilleTemps createdFeuilleTemps = planningService.creerEtAssocierFeuilleTemps(planningId, userId, feuilleTemps);
        return new ResponseEntity<>(createdFeuilleTemps, HttpStatus.CREATED);
    }

    @GetMapping("/allDetails")
    public ResponseEntity<List<Planning>> getAllPlanningsWithDetails() {
        List<Planning> plannings = planningService.obtenirTousLesPlanningsAvecDetails();
        return new ResponseEntity<>(plannings, HttpStatus.OK);
    }

    @GetMapping("/byname/{nomProjet}")
    public ResponseEntity<Planning> obtenirPlanningParNomProjet(@PathVariable String nomProjet) {
        Planning planning = planningService.obtenirPlanningParNomProjet(nomProjet);
        return new ResponseEntity<>(planning, HttpStatus.OK);
    }


    @GetMapping("/feuillestemps")
    public ResponseEntity<List<FeuilleTemps>> obtenirFeuillesDeTemps(@RequestParam(required = false) Long planningId,
                                                                     @RequestParam(required = false) Long employeId) {
        List<FeuilleTemps> feuillesDeTemps = planningService.obtenirFeuillesDeTemps(planningId, employeId);
        return new ResponseEntity<>(feuillesDeTemps, HttpStatus.OK);
    }

    @GetMapping("/feuillestempsForUser")
    public ResponseEntity<List<FeuilleTemps>> obtenirFeuillesDeTempsForUser(@RequestParam(required = false) Long planningId,
                                                                     @RequestParam(required = false) Long userId) {
        List<FeuilleTemps> feuillesDeTemps = planningService.obtenirFeuillesDeTempsForUser(planningId, userId);
        return new ResponseEntity<>(feuillesDeTemps, HttpStatus.OK);
    }

    @PutMapping("/feuillestemps/{feuilleTempsId}/approuver")
    public ResponseEntity<FeuilleTemps> approuverFeuilleDeTemps(@PathVariable Long feuilleTempsId) {
        FeuilleTemps updatedFeuilleTemps = planningService.changerApprobationFeuilleTemps(feuilleTempsId);
        return new ResponseEntity<>(updatedFeuilleTemps, HttpStatus.OK);
    }



    @GetMapping("/{employeId}/total-heures-travaillees")
    public ResponseEntity<Long> calculerTotalHeuresTravailleesPourEmploye(@PathVariable Long employeId) {
        Long totalHeures = planningService.calculerTotalHeuresTravailleesPourEmploye(employeId);
        return new ResponseEntity<>(totalHeures, HttpStatus.OK);
    }


    @GetMapping("/planning/by-project-name/{nomProjet}/employee-ranking")
    public ResponseEntity<?> getEmployeeRankingByHours(@PathVariable String nomProjet) {
        try {
            List<Map<String, Object>> rankings = planningService.getEmployeeRankingByHours(nomProjet);
            return new ResponseEntity<>(rankings, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Planning not found with project name: " + nomProjet);
        }
    }
}