package com.example.pidev.Entities;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Employe implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_id", unique = true, nullable = false)
    private Long empId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Temporal(TemporalType.DATE)
    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "end_date")
    private Date endDate;

    @Column
    private String title;

    private BigDecimal tauxHoraire;
    private BigDecimal tauxHeuresSupplementaires;
    private BigDecimal montantPrimes;
    private BigDecimal montantDeductions;

    @ManyToOne
    @JsonIgnore
    private Departement departement;

    @OneToMany(mappedBy = "employe")
    @JsonIgnore
    private Set<Paie> Paiess;

    @ManyToMany(mappedBy = "employesAffectes")
    private Set<Planning> plannings;


    @ManyToMany(mappedBy = "participants")
    @JsonIgnore
    private Set<SessionFormation> sessionsFormation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "iduser")
    @JsonIgnore
    private User user;

    @OneToOne(mappedBy = "employe")
    @JsonIgnore
    private DemandeDémission demandeDémission;

    @OneToMany(mappedBy = "employe")
    @JsonIgnore
    private Set<DemandeConge> demandesConge;

    @OneToMany(mappedBy = "employe")
    @JsonIgnore
    private Set<DemandeParticipationConvention>demandeParticipationConventions ;

    @OneToMany(mappedBy = "employe")
    @JsonIgnore
    private Set<DemandeParticipationEvenement> demandeParticipationEvenements;

    @OneToMany(mappedBy = "employe")
    @JsonIgnore
    private Set<Paie> paies;


    @OneToMany(mappedBy = "employe", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Depense> depenses = new HashSet<>();

    @OneToMany(mappedBy = "employe", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<ObjectifEpargne> objectifsEpargne = new HashSet<>();

    @OneToMany(mappedBy = "employe", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<AnalyseFinanciere> analysesFinancieres = new HashSet<>();
}
