package com.example.pidev.Repositories;

import com.example.pidev.Entities.Evenement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EvenementRepository extends JpaRepository<Evenement, Long> {
    List<Evenement> findByPartenaireId(Long partenaireId);

    @Query("SELECT COALESCE(MONTH(e.dateHeure), 0) AS month, COUNT(d) AS count FROM Evenement e JOIN e.demandeParticipationevenments d WHERE d.estValidee = true GROUP BY COALESCE(MONTH(e.dateHeure), 0)")
    List<Object[]> countValidatedParticipationsByMonthForEvents();


}
