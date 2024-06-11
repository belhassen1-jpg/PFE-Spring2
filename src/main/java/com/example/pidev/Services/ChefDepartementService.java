package com.example.pidev.Services;

import com.example.pidev.Entities.ChefDepartement;
import com.example.pidev.Entities.Departement;
import com.example.pidev.Entities.Employe;
import com.example.pidev.Repositories.ChefDepartementRepository;
import com.example.pidev.Repositories.DepartementRepository;
import com.example.pidev.Repositories.EmployeRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class ChefDepartementService {
    @Autowired
    private ChefDepartementRepository chefDepartementRepository;
    @Autowired
    private DepartementRepository departementRepository;
    @Autowired
    private  EmployeRepository employeRepository;


    @Transactional
    public ChefDepartement assignChefToDepartement(Long employeId, ChefDepartement chefDepartementDetails, Long departementId) {
        Employe employe = employeRepository.findById(employeId)
                .orElseThrow(() -> new EntityNotFoundException("Employe non trouvé"));

        Departement departement = departementRepository.findById(departementId)
                .orElseThrow(() -> new EntityNotFoundException("Département non trouvé"));

        // Create a new ChefDepartement from the details
        ChefDepartement chefDepartement = new ChefDepartement();
        chefDepartement.setSpecialisation(chefDepartementDetails.getSpecialisation());
        chefDepartement.setEmploye(employe);
        chefDepartement.setDepartementDirige(departement);
        employe.setTitle("Chef Departement");
        // Save the ChefDepartement
        chefDepartementRepository.save(chefDepartement);
        employeRepository.save(employe);
        // Update the department with its new chef
        departement.setChefDepartement(chefDepartement);
        departementRepository.save(departement);

        return chefDepartement;
    }

    public List<ChefDepartement> findAllChefDepartements() {
        return chefDepartementRepository.findAll();
    }


    public ChefDepartement getChefDepartementById(Long id) {
        return chefDepartementRepository.findById(id).orElse(null);
    }

    public void deleteChefDepartement(Long id) {
        chefDepartementRepository.deleteById(id);
    }
}
