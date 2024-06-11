package com.example.pidev;

import com.example.pidev.Entities.Departement;
import com.example.pidev.Entities.Employe;
import com.example.pidev.Entities.User;
import com.example.pidev.Repositories.DepartementRepository;
import com.example.pidev.Repositories.EmployeRepository;
import com.example.pidev.Repositories.UserRepository;
import com.example.pidev.Services.EmployeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeServiceTest {

    @Mock
    private EmployeRepository employeRepository;

    @Mock
    private DepartementRepository departementRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EmployeService employeService;

    private Employe employe;
    private Departement departement;
    private User user;

    @BeforeEach
    void setUp() {
        employe = new Employe();
        employe.setEmpId(1L);
        employe.setFirstName("John");
        employe.setLastName("Doe");

        departement = new Departement();
        departement.setId(1L);
        departement.setNom("IT");

        user = new User();
        user.setIdUser(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
    }

    @Test
    void testAjouterEmployeAvecDepartement() {
        when(departementRepository.findById(anyLong())).thenReturn(Optional.of(departement));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(employeRepository.save(any(Employe.class))).thenReturn(employe);

        Employe savedEmploye = employeService.ajouterEmployeAvecDepartement(employe, 1L, 1L);

        assertNotNull(savedEmploye);
        assertEquals(employe.getFirstName(), savedEmploye.getFirstName());
        verify(departementRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
        verify(employeRepository, times(1)).save(any(Employe.class));
    }

    @Test
    void testGetAllEmployes() {
        when(employeRepository.findAll()).thenReturn(List.of(employe));

        List<Employe> employes = employeService.getAllEmployes();

        assertFalse(employes.isEmpty());
        verify(employeRepository, times(1)).findAll();
    }

    @Test
    void testGetEmployeById() {
        when(employeRepository.findById(anyLong())).thenReturn(Optional.of(employe));

        Employe foundEmploye = employeService.getEmployeById(1L);

        assertNotNull(foundEmploye);
        assertEquals(employe.getFirstName(), foundEmploye.getFirstName());
        verify(employeRepository, times(1)).findById(anyLong());
    }

    @Test
    void testGetEmployeById_NotFound() {
        when(employeRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> employeService.getEmployeById(1L));
        verify(employeRepository, times(1)).findById(anyLong());
    }

    @Test
    void testSaveOrUpdateEmploye() {
        when(employeRepository.findById(anyLong())).thenReturn(Optional.of(employe));
        when(employeRepository.save(any(Employe.class))).thenReturn(employe);

        Employe updatedEmploye = employeService.saveOrUpdateEmploye(employe);

        assertNotNull(updatedEmploye);
        verify(employeRepository, times(1)).findById(anyLong());
        verify(employeRepository, times(1)).save(any(Employe.class));
    }

    @Test
    void testDeleteEmploye() {
        doNothing().when(employeRepository).deleteById(anyLong());

        employeService.deleteEmploye(1L);

        verify(employeRepository, times(1)).deleteById(anyLong());
    }
}
