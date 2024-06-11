package com.example.pidev.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
public class DeclarationFiscale implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date dateDeclaration;

    private BigDecimal totalRevenuImposable;
    private BigDecimal montantImpotDu;
    private BigDecimal montantCotisationsSocialesDu;
    private String referenceDeclaration;
    private String autoriteFiscale;


    @OneToOne
    @JoinColumn(name = "bulletinPaie_id", referencedColumnName = "id")
    private BulletinPaie bulletinPaie;

}
