package com.example.pidev.Controllers;
import com.example.pidev.Entities.Employe;
import com.example.pidev.Entities.Rapport;
import com.example.pidev.Entities.User;
import com.example.pidev.Repositories.EmployeRepository;
import com.example.pidev.Repositories.UserRepository;
import com.example.pidev.Services.RapportService;
import com.itextpdf.text.DocumentException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.persistence.EntityNotFoundException;

@RestController
@AllArgsConstructor
@NoArgsConstructor
@RequestMapping("/api/rapports")
public class RapportController {

    @Autowired
    private RapportService rapportService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmployeRepository employeRepository;
    @GetMapping(value = "/{employeId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<ByteArrayResource> genererOuMettreAJourRapport(@PathVariable Long employeId) {
        try {
            Rapport rapport = rapportService.genererOuMettreAJourRapport(employeId);
            byte[] pdfContents = rapportService.genererPdfRapport(rapport);

            ByteArrayResource byteArrayResource = new ByteArrayResource(pdfContents);
            return ResponseEntity
                    .ok()
                    .contentLength(byteArrayResource.contentLength())
                    .header("Content-Disposition", "attachment; filename=rapport_" + employeId + ".pdf")
                    .body(byteArrayResource);
        } catch (DocumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{employeId}/download")
    public ResponseEntity<byte[]> downloadRapportAsPdf(@PathVariable Long employeId) {
        try {
            Rapport rapport = rapportService.genererOuMettreAJourRapport(employeId);
            byte[] pdfContent = rapportService.genererPdfRapport(rapport);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            String filename = "rapport_" + employeId + ".pdf";
            headers.setContentDisposition(ContentDisposition.builder("attachment").filename(filename).build());

            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
        } catch (RuntimeException | DocumentException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/user/{userId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<ByteArrayResource> genererOuMettreAJourRapportForUser(@PathVariable Long userId) {
        try {
            Rapport rapport = rapportService.genererOuMettreAJourRapportForUser(userId);
            byte[] pdfContents = rapportService.genererPdfRapport(rapport);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
            Employe employe = employeRepository.findByUser(user)
                    .orElseThrow(() -> new EntityNotFoundException("Employé non trouvé pour cet utilisateur"));

            ByteArrayResource byteArrayResource = new ByteArrayResource(pdfContents);
            return ResponseEntity
                    .ok()
                    .contentLength(byteArrayResource.contentLength())
                    .header("Content-Disposition", "attachment; filename=rapport_"
                            + employe.getEmpId() + ".pdf")
                    .body(byteArrayResource);
        } catch (DocumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }



}