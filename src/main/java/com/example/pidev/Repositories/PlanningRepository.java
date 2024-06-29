package com.example.pidev.Repositories;

import com.example.pidev.Entities.Planning;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PlanningRepository extends JpaRepository<Planning, Long> {
    @Query("SELECT p FROM Planning p")
    List<Planning> findAllWithDetails();
    Optional<Planning> findByNomProjet(String nomProjet);
    @Query("SELECT p FROM Planning p JOIN p.employesAffectes e WHERE e.empId = :employeeId")
    List<Planning> findByEmployeeId(Long employeeId);

}