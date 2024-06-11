package com.example.pidev.Entities;
import lombok.*;
import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@PrimaryKeyJoinColumn(name = "id")
public class BulletinPaie extends Paie {

    @Temporal(TemporalType.DATE)
    private Date dateEmission;

    @Temporal(TemporalType.DATE)
    private Date periodeDebut;

    @Temporal(TemporalType.DATE)
    private Date periodeFin;

    private String commentaire;

    private String nomEntreprise;
    private String adresseEntreprise;


    @OneToOne
    @JoinColumn(name = "rapport_id", referencedColumnName = "id")
    private Rapport rapport;
    @OneToOne
    @JoinColumn(name = "declarationFiscale_id", referencedColumnName = "id")
    private DeclarationFiscale declarationFiscale;


}
