package com.example.pidev.Services;

import com.example.pidev.Entities.Partenaire;
import com.example.pidev.Repositories.PartenaireRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class PartenaireService {
    @Autowired
    private PartenaireRepository partenaireRepository;

    public List<Partenaire> findAllPartenaires() {
        return partenaireRepository.findAll();
    }

    public Optional<Partenaire> findPartenaireById(Long id) {
        return partenaireRepository.findById(id);
    }

    public Partenaire savePartenaire(Partenaire partenaire) {
        return partenaireRepository.save(partenaire);
    }

    public void deletePartenaire(Long id) {
        partenaireRepository.deleteById(id);
    }

    public List<Partenaire> getPartenairesRankedByInvolvement() {
        return findAllPartenaires().stream()
                .map(p -> {
                    p.setTotalParticipations(p.getConventions().size() + p.getEvenements().size());
                    return p;
                })
                .sorted((p1, p2) -> Integer.compare(
                        p2.getTotalParticipations(),
                        p1.getTotalParticipations()))
                .collect(Collectors.toList());
    }

}
