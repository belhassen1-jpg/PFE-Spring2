package com.example.pidev;

import com.example.pidev.Entities.*;
import com.example.pidev.Repositories.ConventionRepository;
import com.example.pidev.Repositories.DemandeParticipationConventionRepository;
import com.example.pidev.Repositories.EmployeRepository;
import com.example.pidev.Repositories.PartenaireRepository;
import com.example.pidev.Services.GestionConventionService;
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
class GestionConventionServiceTest {

    @Mock
    private ConventionRepository conventionRepository;

    @Mock
    private PartenaireRepository partenaireRepository;

    @Mock
    private DemandeParticipationConventionRepository demandeRepository;

    @Mock
    private EmployeRepository employeRepository;

    @InjectMocks
    private GestionConventionService gestionConventionService;

    private Convention convention;
    private Partenaire partenaire;
    private Employe employe;
    private DemandeParticipationConvention demande;

    @BeforeEach
    void setUp() {
        convention = new Convention();
        convention.setId(1L);

        partenaire = new Partenaire();
        partenaire.setId(1L);

        employe = new Employe();
        employe.setEmpId(1L);

        demande = new DemandeParticipationConvention();
        demande.setId(1L);
        demande.setEmploye(employe);
        demande.setConvention(convention);
    }

    @Test
    void testSaveOrUpdateConvention() {
        when(partenaireRepository.findById(anyLong())).thenReturn(Optional.of(partenaire));
        when(conventionRepository.save(any(Convention.class))).thenReturn(convention);

        Convention savedConvention = gestionConventionService.saveOrUpdateConvention(convention, 1L);

        assertNotNull(savedConvention);
        assertEquals(convention.getId(), savedConvention.getId());
        verify(partenaireRepository, times(1)).findById(anyLong());
        verify(conventionRepository, times(1)).save(any(Convention.class));
    }

    @Test
    void testCreerDemandeParticipation() {
        when(employeRepository.findEmployeByUser_IdUser(anyLong())).thenReturn(Optional.of(employe));
        when(conventionRepository.findById(anyLong())).thenReturn(Optional.of(convention));
        when(demandeRepository.save(any(DemandeParticipationConvention.class))).thenReturn(demande);

        DemandeParticipationConvention savedDemande = gestionConventionService.creerDemandeParticipation(1L, 1L);

        assertNotNull(savedDemande);
        assertEquals(demande.getId(), savedDemande.getId());
        verify(employeRepository, times(1)).findEmployeByUser_IdUser(anyLong());
        verify(conventionRepository, times(1)).findById(anyLong());
        verify(demandeRepository, times(1)).save(any(DemandeParticipationConvention.class));
    }



    @Test
    void testGetDemandeConvention() {
        when(demandeRepository.findAll()).thenReturn(List.of(demande));

        List<DemandeParticipationConvention> demandes = gestionConventionService.getDemandeConvention();

        assertFalse(demandes.isEmpty());
        verify(demandeRepository, times(1)).findAll();
    }

    @Test
    void testTrouverConventionsEtParticipants() {
        when(conventionRepository.findAll()).thenReturn(List.of(convention));

        List<Convention> conventions = gestionConventionService.trouverConventionsEtParticipants();

        assertFalse(conventions.isEmpty());
        verify(conventionRepository, times(1)).findAll();
    }

    @Test
    void testTrouverConventionsParPartenaire() {
        when(conventionRepository.findByPartenaireId(anyLong())).thenReturn(List.of(convention));

        List<Convention> conventions = gestionConventionService.trouverConventionsParPartenaire(1L);

        assertFalse(conventions.isEmpty());
        verify(conventionRepository, times(1)).findByPartenaireId(anyLong());
    }

    @Test
    void testTrouverEmployesParConvention() {
        when(demandeRepository.findByConventionIdAndEstValidee(anyLong(), eq(true))).thenReturn(List.of(demande));

        List<DemandeParticipationConvention> demandes = gestionConventionService.trouverEmployesParConvention(1L);

        assertFalse(demandes.isEmpty());
        verify(demandeRepository, times(1)).findByConventionIdAndEstValidee(anyLong(), eq(true));
    }

    @Test
    void testRetirerEmployeDesConventions() {
        when(demandeRepository.findByEmployeId(anyLong())).thenReturn(List.of(demande));
        doNothing().when(demandeRepository).delete(any(DemandeParticipationConvention.class));

        gestionConventionService.retirerEmployeDesConventions(1L);

        verify(demandeRepository, times(1)).findByEmployeId(anyLong());
        verify(demandeRepository, times(1)).delete(any(DemandeParticipationConvention.class));
    }


}
