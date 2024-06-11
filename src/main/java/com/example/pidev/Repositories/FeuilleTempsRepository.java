package com.example.pidev.Repositories;
import com.example.pidev.Entities.FeuilleTemps;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface FeuilleTempsRepository extends JpaRepository<FeuilleTemps, Long> {
    List<FeuilleTemps> findByPlanningId(Long planningId);

    @Query("SELECT ft FROM FeuilleTemps ft JOIN ft.planning p JOIN p.employesAffectes e WHERE e.empId = :employeId")
    List<FeuilleTemps> findByEmployeId(@Param("employeId") Long employeId);

    @Query("SELECT ft FROM FeuilleTemps ft JOIN ft.planning p JOIN p.employesAffectes e WHERE e.empId = :employeId AND ft.jour BETWEEN :dateDebut AND :dateFin")
    List<FeuilleTemps> findByEmployeIdAndPeriode(@Param("employeId") Long employeId, @Param("dateDebut") Date dateDebut, @Param("dateFin") Date dateFin);
}