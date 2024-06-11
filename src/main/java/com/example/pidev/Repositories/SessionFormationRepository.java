package com.example.pidev.Repositories;

import com.example.pidev.Entities.SessionFormation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SessionFormationRepository extends JpaRepository<SessionFormation, Long> {
    @Query("SELECT COALESCE(MONTH(f.dateHeure), 0) AS month, COUNT(p) AS count FROM SessionFormation f JOIN f.participants p GROUP BY COALESCE(MONTH(f.dateHeure), 0)")
    List<Object[]> countParticipationsByMonthForFormations();

}
