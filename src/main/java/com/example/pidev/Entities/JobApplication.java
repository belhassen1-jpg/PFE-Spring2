package com.example.pidev.Entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class JobApplication implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String applicantName;
    private String applicantEmail;
    private Long applicantPhone;
    private String applicantAddress;
    private Integer yearsOfExperience;
    private String resumePath;
    private String coverLetterPath;

    @Enumerated(EnumType.STRING)
    private StatutDemande status;
    @ManyToOne
    @JoinColumn(name = "job_offer_id")
    private JobOffer jobOffer;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User applicant;


}

