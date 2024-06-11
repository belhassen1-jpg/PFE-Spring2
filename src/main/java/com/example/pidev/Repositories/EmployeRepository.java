package com.example.pidev.Repositories;

import com.example.pidev.Entities.Departement;
import com.example.pidev.Entities.Employe;
import com.example.pidev.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeRepository extends JpaRepository<Employe, Long> {
    long countByDepartement(Departement departement);

    Optional<Employe> findByUser(User user);
    Optional<Employe> findEmployeByUser_IdUser(Long userId);
}
