package com.example.pidev.Entities;
import lombok.*;
import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Inheritance(strategy = InheritanceType.JOINED)
public class Paie implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Temporal(TemporalType.DATE)
    private Date datePaie;

    private BigDecimal tauxHoraire;
    private BigDecimal heuresTravaillees;
    private BigDecimal heuresSupplementaires;
    private BigDecimal tauxHeuresSupplementaires;
    private BigDecimal montantPrimes;
    private BigDecimal montantDeductions;

    private BigDecimal cotisationsSociales;
    private BigDecimal impotSurRevenu;
    private BigDecimal salaireNet;
    private BigDecimal SalaireBrut;



    @ManyToOne
    @JoinColumn(name = "employe_id")
    private Employe employe;
}
