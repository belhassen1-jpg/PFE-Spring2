package com.example.pidev.Services;
import com.example.pidev.Entities.User;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class FileStorageService {

   @Value("${app.file.storage.location}")

    private String storageLocation;

    public String storeFile(MultipartFile file, User user) throws IOException {

        String fileName = StringUtils.cleanPath(user.getFirstName() + "-" + user.getLastName() + "-" + file.getOriginalFilename());


        Path storagePath = Paths.get(storageLocation).toAbsolutePath().normalize();
        Path targetLocation = storagePath.resolve(fileName);


        Files.createDirectories(storagePath);


        Files.copy(file.getInputStream(), targetLocation);


        return fileName;
    }
}
