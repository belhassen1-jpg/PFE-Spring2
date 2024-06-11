package com.example.pidev.Controllers;

import com.example.pidev.Entities.ChefDepartement;
import com.example.pidev.Services.ChefDepartementService;
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
@RequestMapping("/api/chefdepartements")

public class ChefDepartementController {
    @Autowired
    private ChefDepartementService chefDepartementService;

    @PostMapping("/assign")
    public ResponseEntity<ChefDepartement> assignChefToDepartement(
            @RequestParam Long employeId,
            @RequestParam Long departementId,
            @RequestBody ChefDepartement chefDepartementDetails) {
        ChefDepartement newChefDepartement = chefDepartementService.assignChefToDepartement(employeId, chefDepartementDetails, departementId);
        return ResponseEntity.status(HttpStatus.CREATED).body(newChefDepartement);
    }

    @GetMapping
    public ResponseEntity<List<ChefDepartement>> getAllChefDepartements() {
        return ResponseEntity.ok(chefDepartementService.findAllChefDepartements());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChefDepartement> getChefDepartementById(@PathVariable Long id) {
        ChefDepartement chefDepartement = chefDepartementService.getChefDepartementById(id);
        return ResponseEntity.ok(chefDepartement);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChefDepartement(@PathVariable Long id) {
        chefDepartementService.deleteChefDepartement(id);
        return ResponseEntity.ok().build();
    }
}
