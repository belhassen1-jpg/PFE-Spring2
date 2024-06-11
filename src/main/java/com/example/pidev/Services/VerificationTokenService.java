package com.example.pidev.Services;

import com.example.pidev.Entities.User;
import com.example.pidev.Entities.VerificationToken;
import com.example.pidev.Repositories.VerificationTokenRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@NoArgsConstructor
public class VerificationTokenService {

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;


    public VerificationTokenService(VerificationTokenRepository verificationTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
    }

    public VerificationToken createVerificationToken(User user) {
        String token = generateVerificationToken();
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(1);
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(expiryDate);
        verificationTokenRepository.save(verificationToken);
        user.setVerificationToken(token);
        return verificationToken;
    }


    public void saveVerificationToken(VerificationToken verificationToken) {
        verificationTokenRepository.save(verificationToken);
    }


    // générer un jeton aléatoire
    public String generateVerificationToken() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int length = 30;
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }

        return sb.toString();
    }
}