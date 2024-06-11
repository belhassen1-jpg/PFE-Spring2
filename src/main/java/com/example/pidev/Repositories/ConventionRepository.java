package com.example.pidev.Repositories;

import com.example.pidev.Entities.Convention;
import com.example.pidev.Entities.Evenement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ConventionRepository extends JpaRepository<Convention, Long> {
    List<Convention> findByPartenaireId(Long partenaireId);

    @Query("SELECT COALESCE(MONTH(c.dateDebut), 0) as month, COUNT(d) as count FROM Convention c JOIN c.demandeParticipationConventions d WHERE d.estValidee = true GROUP BY MONTH(c.dateDebut)")
    List<Object[]> countValidatedParticipationsByMonthForConventions();



}
