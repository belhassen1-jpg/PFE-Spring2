package com.example.pidev.Services;
import com.example.pidev.Entities.*;
import com.example.pidev.Repositories.EmployeRepository;
import com.example.pidev.Repositories.SessionFormationRepository;
import com.example.pidev.Repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class SessionFormationService {
    @Autowired
    private SessionFormationRepository sessionFormationRepository;
    @Autowired
    private EmployeRepository employeRepository;
    @Autowired
    private UserRepository userRepository;

    public SessionFormation creerSessionFormation(SessionFormation sessionFormation) {
        return sessionFormationRepository.save(sessionFormation);
    }

    public void deleteFormation(Long id) {
        sessionFormationRepository.deleteById(id);
    }

    public List<SessionFormation> getAllFormations() {
        return sessionFormationRepository.findAll();
    }

    public SessionFormation obtenirSessionFormationParId(Long id) {
        return sessionFormationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Session de formation non trouvée avec l'id : " + id));
    }

    public List<SessionFormation> getAllSessionsWithParticipants() {
        List<SessionFormation> sessions = sessionFormationRepository.findAll();
        sessions.forEach(session -> Hibernate.initialize(session.getParticipants()));
        return sessions;
    }

    public SessionFormation inscrireEmployeASession(Long sessionId, Long userId) {
        SessionFormation session = sessionFormationRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session de formation non trouvée avec l'id : " + sessionId));
        Employe employe = employeRepository.findEmployeByUser_IdUser(userId)
                .orElseThrow(() -> new EntityNotFoundException("Employé non trouvé avec l'id : " + userId));

        session.getParticipants().add(employe);
        return sessionFormationRepository.save(session);
    }

    public int compterFormationsParticipeesParEmploye(Long employeId) {
        Employe employe = employeRepository.findById(employeId)
                .orElseThrow(() -> new EntityNotFoundException("Employé non trouvé avec l'id : " + employeId));

        return (int) sessionFormationRepository.findAll().stream()
                .filter(session -> session.getParticipants().contains(employe))
                .count();
    }

    public int compterFormationsParticipesParUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
        Employe employe = employeRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Employé non trouvé pour cet utilisateur"));
        return (int) sessionFormationRepository.findAll().stream()
                .filter(session -> session.getParticipants().contains(employe))
                .count();
    }
}
