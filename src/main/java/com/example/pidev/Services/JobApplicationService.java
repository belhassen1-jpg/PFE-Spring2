package com.example.pidev.Services;
import com.example.pidev.DTO.JobApplicationDto;
import com.example.pidev.Entities.JobApplication;
import com.example.pidev.Entities.JobOffer;
import com.example.pidev.Entities.StatutDemande;
import com.example.pidev.Entities.User;
import com.example.pidev.Repositories.JobApplicationRepository;
import com.example.pidev.Repositories.JobOfferRepository;
import com.example.pidev.Repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.core.io.Resource;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class JobApplicationService {
    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobOfferRepository jobOfferRepository;

    @Autowired
    private JavaMailSender mailSender;


    private static final String RH_EMAIL = "belhassen.knani@esprit.tn";


    public List<JobApplication> getApplicationsList() {
        return jobApplicationRepository.findAll();
    }


    public List<JobApplication> getApplicationsByUserId(Long userId) {
        return jobApplicationRepository.findByApplicantId(userId);
    }


    public List<JobApplication> getApplicationsForJobOffer(Long jobOfferId) {
        return jobApplicationRepository.findByJobOfferId(jobOfferId);
    }


    @Transactional
    public JobApplicationDto submitApplication(JobApplicationDto applicationDto, String resumePath, String coverLetterPath) {
        JobApplication application = new JobApplication();

        JobOffer jobOffer = jobOfferRepository.findById(applicationDto.getJobOfferId())
                .orElseThrow(() -> new RuntimeException("Offre d'emploi non trouvée."));
        User applicant = userRepository.findById(applicationDto.getUserId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé."));


        BeanUtils.copyProperties(applicationDto, application);


        application.setResumePath(resumePath);
        application.setCoverLetterPath(coverLetterPath);


        application.setJobOffer(jobOffer);
        application.setApplicant(applicant);


        application.setStatus(StatutDemande.EN_ATTENTE);


        JobApplication savedApplication = jobApplicationRepository.save(application);


        sendEmailToRH(savedApplication);

        // Convertir l'entité sauvegardée en DTO pour le retour
        JobApplicationDto savedDto = new JobApplicationDto();
        BeanUtils.copyProperties(savedApplication, savedDto);
        savedDto.setJobOfferId(savedApplication.getJobOffer().getId());
        savedDto.setUserId(savedApplication.getApplicant().getIdUser());

        return savedDto;
    }

    private void sendEmailToRH(JobApplication application) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@example.com");
        message.setTo(RH_EMAIL);
        message.setSubject("Nouvelle candidature soumise");
        message.setText(createEmailContent(application));
        mailSender.send(message);
    }

    private String createEmailContent(JobApplication application) {
        return String.format(
                "Une nouvelle candidature a été soumise pour l'offre d'emploi: %s\n" +
                        "Nom du candidat: %s\n" +
                        "Email: %s\n" +
                        "Téléphone: %s\n" +
                        "Adresse: %s\n" +
                        "Années d'expérience: %d\n" +
                        "CV: %s\n" +
                        "Lettre de motivation: %s\n",
                application.getJobOffer().getTitle(),
                application.getApplicantName(),
                application.getApplicantEmail(),
                application.getApplicantPhone(),
                application.getApplicantAddress(),
                application.getYearsOfExperience(),
                application.getResumePath(),
                application.getCoverLetterPath()
        );
    }


    @Transactional
    public void updateApplicationStatus(Long applicationId, StatutDemande newStatus) {
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found."));

        application.setStatus(newStatus);
        jobApplicationRepository.save(application);

        sendStatusUpdateEmail(application);
    }

    private void sendStatusUpdateEmail(JobApplication application) {
        String subject;
        String content;

        if (application.getStatus() == StatutDemande.ACCEPTEE) {
            subject = "Your application has been accepted!";
            content = "Congratulations! Your application for the position of " + application.getJobOffer().getTitle() + " has been accepted.";
        } else if (application.getStatus() == StatutDemande.REFUSEE) {
            subject = "Your application has been rejected";
            content = "We regret to inform you that your application for the position of " + application.getJobOffer().getTitle() + " has been rejected.";
        } else {
            // Handle other statuses if needed
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(application.getApplicantEmail());
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }

    public Resource loadFileAsResource(String filePath) throws IOException {
        try {
            Path fileStorageLocation = Paths.get("C:\\Users\\Belhassen\\Desktop\\belha\\PFE-belhassen\\PFE-belhassen\\uploads").toAbsolutePath().normalize();
            Path filePathInStorage = fileStorageLocation.resolve(filePath).normalize();
            Resource resource = new UrlResource(filePathInStorage.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new IOException("File not found " + filePath);
            }
        } catch (MalformedURLException ex) {
            throw new IOException("File not found " + filePath, ex);
        }
    }

}
