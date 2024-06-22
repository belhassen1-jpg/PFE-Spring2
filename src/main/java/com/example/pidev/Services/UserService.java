package com.example.pidev.Services;
import com.example.pidev.Entities.*;
import com.example.pidev.Interfaces.IUserService;
import com.example.pidev.Repositories.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.mail.MessagingException;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;


@Service
@AllArgsConstructor
@NoArgsConstructor

public class UserService implements IUserService {

    @Autowired
    VerificationTokenService verificationTokenService;

    @Autowired
    ImplEmailService emailService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserCodeRepository userCodeRepository;



    @Override
    public List<User> retrieveAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(User user) {

        User newUser = userRepository.findById(user.getIdUser()).get();
        newUser.setAge(user.getAge());
        newUser.setCIN(user.getCIN());
        newUser.setUsername(user.getUsername());
        newUser.setEmail(user.getEmail());
        newUser.setPhone(user.getPhone());
        newUser.setUserJob(user.getUserJob());
        return userRepository.save(newUser);
    }

    @Override
    public User updateUser2(Long id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
        existingUser.setAge(user.getAge());
        existingUser.setCIN(user.getCIN());
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());
        existingUser.setUserJob(user.getUserJob());
        return userRepository.save(existingUser);
    }

    @Override
    public User retrieveUser(Long IdUser) {
        return userRepository.findById(IdUser).get();
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User retrieveUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }


    public User retrieveByVerificationCode(String code) {
        return userRepository.findByVerificationCode(code);
    }

    ////////SIGN-UP/////////
    @Override
    public User registerUser(User user) {
        user.setIsVerified(0);
        user.setPassword(getEncodedPassword(user.getPassword()));
        Date dateNaissance = user.getBirthDate();
        LocalDate localDateNaissance = dateNaissance.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate dateSysteme = LocalDate.now();
        int age = Period.between(localDateNaissance, dateSysteme).getYears();
        user.setAge(age);
        user.setUserJob("User");
            userRepository.save(user);
        return user;
    }

    @Override
    public User retrieveUserByPhone(Long Phone) {
        return userRepository.findByPhone(Phone);
    }


    //////User Verification //////
    public User VerifyUser(String token) {
        User user = userRepository.findByVerificationToken(token);
        if (user != null) {
            user.setIsVerified(1);
            user.setVerificationToken(null);
            userRepository.save(user);
        }
        return user;
    }

    //cryptage
    public String getEncodedPassword(String password) {
        return passwordEncoder.encode(password);
    }


    public User ResetPasswordSms(String code, String newPassword, String confirmPassword) {
        User user = userRepository.findByVerificationCode(code);
        UserCode userCode = userCodeRepository.findByCode(code);
        if (user != null) {
            if (user.getVerificationCode().equals(userCode.getCode())) {
                if (newPassword.equals(confirmPassword)) {
                    user.setPassword(passwordEncoder.encode(newPassword));

                    userRepository.save(user);
                } else {
                    throw new IllegalArgumentException("Passwords do not match");
                }
            } else {
                throw new IllegalArgumentException("User not found");
            }
        } else {
            throw new IllegalArgumentException("Verification code is invalid");
        }
        return user;

    }


}





