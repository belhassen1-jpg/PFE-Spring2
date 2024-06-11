package com.example.pidev.Entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Planning implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nomProjet;
    @Temporal(TemporalType.DATE)
    private Date dateDebutValidite;

    @Temporal(TemporalType.DATE)
    private Date dateFinValidite;

    @OneToMany(mappedBy = "planning")
    @JsonIgnore
    private List<FeuilleTemps> feuillesDeTemps;

    @ManyToMany
    @JsonIgnore
    @JoinTable(
            name = "planning_employes",
            joinColumns = @JoinColumn(name = "planning_id"),
            inverseJoinColumns = @JoinColumn(name = "employe_id")
    )
    private Set<Employe> employesAffectes;
}