package com.example.pidev.Services;
import com.example.pidev.Repositories.ConventionRepository;
import com.example.pidev.Repositories.EvenementRepository;
import com.example.pidev.Repositories.SessionFormationRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class StatistiquesService {
    @Autowired
    private ConventionRepository conventionRepository;

    @Autowired
    private EvenementRepository evenementRepository;

    @Autowired
    private SessionFormationRepository formationRepository;

    public Map<String, Map<Integer, Long>> gatherStatistics() {
        Map<String, Map<Integer, Long>> stats = new HashMap<>();

        List<Object[]> conventionData = conventionRepository.countValidatedParticipationsByMonthForConventions();
        List<Object[]> eventData = evenementRepository.countValidatedParticipationsByMonthForEvents();
        List<Object[]> formationData = formationRepository.countParticipationsByMonthForFormations();

        stats.put("Conventions", convertListToMap(conventionData));
        stats.put("Events", convertListToMap(eventData));
        stats.put("Formations", convertListToMap(formationData));

        return stats;
    }

    private Map<Integer, Long> convertListToMap(List<Object[]> data) {
        Map<Integer, Long> result = new HashMap<>();
        data.forEach(item -> result.put((Integer) item[0], ((Long) item[1])));
        return result;
    }
}
