package com.example.pidev.DTO;

import com.example.pidev.Entities.StatutDemande;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class JobApplicationDto {
    private Long jobOfferId;
    private Long userId;
    private String applicantName;
    private String applicantEmail;
    private Long applicantPhone;
    private String applicantAddress;
    private Integer yearsOfExperience;
    private String resumePath;
    private String coverLetterPath;
    private StatutDemande status;

}
