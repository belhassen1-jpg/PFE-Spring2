package com.example.pidev.Controllers;
import com.example.pidev.Services.StatistiquesService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@AllArgsConstructor
@NoArgsConstructor
@RequestMapping("/api/statistics")

public class StatistiquesController {
        @Autowired
        private StatistiquesService statistiquesService;

        @GetMapping("/participation")
        public ResponseEntity<Map<String, Map<Integer, Long>>> getParticipationStatistics() {
                Map<String, Map<Integer, Long>> stats = statistiquesService.gatherStatistics();
                return ResponseEntity.ok(stats);
        }
    }

