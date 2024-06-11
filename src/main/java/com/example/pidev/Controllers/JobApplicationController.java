package com.example.pidev.Controllers;

import com.example.pidev.DTO.JobApplicationDto;
import com.example.pidev.Entities.JobApplication;
import com.example.pidev.Entities.StatutDemande;
import com.example.pidev.Entities.User;
import com.example.pidev.Repositories.JobApplicationRepository;
import com.example.pidev.Repositories.UserRepository;
import com.example.pidev.Services.FileStorageService;
import com.example.pidev.Services.JobApplicationService;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.List;

@RestController
@RequestMapping("/applications")
public class JobApplicationController {
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private JobApplicationService jobApplicationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @GetMapping("/download/resume/{jobApplicationId}")
    public ResponseEntity<Resource> downloadResume(@PathVariable Long jobApplicationId) {
        try {
            JobApplication application = jobApplicationRepository.findById(jobApplicationId)
                    .orElseThrow(() -> new RuntimeException("Application not found."));
            String resumeFilePath = application.getResumePath();

            Resource resource = jobApplicationService.loadFileAsResource(resumeFilePath);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/download/cover-letter/{jobApplicationId}")
    public ResponseEntity<Resource> downloadCoverLetter(@PathVariable Long jobApplicationId) {
        try {
            JobApplication application = jobApplicationRepository.findById(jobApplicationId)
                    .orElseThrow(() -> new RuntimeException("Application not found."));
            String coverLetterFilePath = application.getCoverLetterPath();
            Resource resource = jobApplicationService.loadFileAsResource(coverLetterFilePath);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/submit")
    public ResponseEntity<JobApplicationDto> submitApplication(
            @RequestParam("jobOfferId") Long jobOfferId,
            @RequestParam("userId") Long userId,
            @RequestParam("resume") MultipartFile resume,
            @RequestParam("coverLetter") MultipartFile coverLetter,
            JobApplicationDto applicationDto) {
        try {

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé."));
            String resumePath = fileStorageService.storeFile(resume,user);
            String coverLetterPath = fileStorageService.storeFile(coverLetter,user);

            JobApplicationDto savedApplicationDto = jobApplicationService.submitApplication(applicationDto, resumePath, coverLetterPath);
            return new ResponseEntity<>(savedApplicationDto, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/updateStatus/{applicationId}")
    public ResponseEntity<String> updateApplicationStatus(
            @PathVariable Long applicationId,
            @RequestParam StatutDemande status) {
        jobApplicationService.updateApplicationStatus(applicationId, status);
        return ResponseEntity.ok("The application status has been updated successfully.");
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<JobApplication>> getApplicationsByUserId(@PathVariable Long userId) {
        List<JobApplication> applications = jobApplicationService.getApplicationsByUserId(userId);
        return ResponseEntity.ok(applications);
    }


    @GetMapping("/job-offer/{jobOfferId}")
    public ResponseEntity<List<JobApplication>> getApplicationsForJobOffer(@PathVariable Long jobOfferId) {
        List<JobApplication> applications = jobApplicationService.getApplicationsForJobOffer(jobOfferId);
        return ResponseEntity.ok(applications);
    }


    @GetMapping("/all")
    public ResponseEntity<List<JobApplication>> getApplicationsList() {
        List<JobApplication> applications = jobApplicationService.getApplicationsList();
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/allForUser/{userId}")
    public ResponseEntity<List<JobApplication>> getApplicationsListForUser(@PathVariable Long userId) {
        List<JobApplication> applications = jobApplicationService.getApplicationsByUserId(userId);
        return ResponseEntity.ok(applications);
    }


}
