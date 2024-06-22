package com.example.pidev.Controllers;

import com.example.pidev.Entities.User;
import com.example.pidev.Entities.UserCode;
import com.example.pidev.Entities.VerificationToken;
import com.example.pidev.Repositories.UserRepository;
import com.example.pidev.Services.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@NoArgsConstructor
public class UserController {

    JavaMailSender mailSender;
    ImplEmailService implEmailService;


    SmsService smsService;
    @Autowired
    UserCodeService codeService;
    @Autowired
    UserService userService;

    @Autowired
    ImplEmailService emailService;
    @Autowired
    UserRepository userRepository;

    @Autowired
    VerificationTokenService verificationTokenService;





    //// Connected User Profile ////////
    @GetMapping("/profile/{username}")
    public User getConnectedUser(@PathVariable String username) {
        User user = userService.retrieveUserByUsername(username);
        return user;
    }




    /////// Sign-UP ///////////
    @PostMapping({"/register"})
    public User registerNewUser(@RequestBody User user)  {



        User NewUser= userService.registerUser(user);
        VerificationToken verificationToken = verificationTokenService.createVerificationToken(user);

        // création du jeton de vérification
        verificationTokenService.saveVerificationToken(verificationToken);

        try {
            emailService.sendVerificationEmail(user);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }


        return NewUser;
    }


    @GetMapping("/verify/{verificationToken}")
    public ResponseEntity<?> activateAccount(@PathVariable String verificationToken) throws javax.mail.MessagingException {
        User user = userService.VerifyUser(verificationToken);
        if (user != null) {
            String to = user.getEmail();
            String subject = "Compte activé";
            try {
                emailService.sendEmail(to, subject);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
            // Créer un objet de réponse personnalisé
            return new ResponseEntity<>("Votre compte a été vérifié avec succès. Veuillez retourner à la page d'accueil et vous connecter.", HttpStatus.OK);

        } else {
            return new ResponseEntity<>("La vérification de votre compte a échoué ou le token n'est plus valide.", HttpStatus.NOT_FOUND);
        }
    }



    ///////Reset Password Sms////////
    @PostMapping("/SendSMS/{Phone}")
    public User SmsSender(@PathVariable Long Phone) {
        User NewUser=userService.retrieveUserByPhone(Phone);
        UserCode userCode = codeService.createVerificationCode(NewUser);
        codeService.saveVerificationCode(userCode);
        NewUser= userService.updateUser(NewUser);

        return NewUser;
    }


    @PutMapping("/reset-password/{verificationCode}")
    public String activateAccount(@PathVariable String verificationCode,@RequestParam String newPassword ,@RequestParam String confirmPassword) throws javax.mail.MessagingException {
        User user = userService.retrieveByVerificationCode(verificationCode);
        if (user != null) {
            user=userService.ResetPasswordSms(verificationCode,newPassword,confirmPassword);
            String to = user.getEmail();
            String subject = "  Account Updated";
            String text = "Congratulations " + user.getUsername() + " Your password account has been updated successfully";
            try {
                emailService.SendResetMail(to,subject,text);
                user.setVerificationCode(null);
                userService.updateUser(user);

            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
            return ("Congratulations " + user.getUsername() + " Your password account has been updated successfully");
        } else {
            return ("there was an error verifying your account, please make sure you have entered the right token and that the token hasn't expried");
        }
    }






//////////////////////////////////////////////////////////////
    @GetMapping("/retrieve-all-Users")
    public List<User> getUsers() {
        List<User> listUsers = userService.retrieveAllUsers();
        return listUsers;

    }
    @GetMapping("/retrieve-user/{iduser}")
    public ResponseEntity<User> getUserDetails(@PathVariable Long iduser) {
        // Add validation to check if userId is a valid Long value
        if (iduser == null) {
            return ResponseEntity.badRequest().build();
        }else
            return ResponseEntity.ok(userService.retrieveUser(iduser));
    }

    @GetMapping("/retrieve/{username}")
    public User getUserByUsername(@PathVariable("username") String username) {
        return userService.retrieveUserByUsername(username);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    @DeleteMapping("/remove-user/{iduser}")
    public void removeUser(@PathVariable("iduser") Long IdUser) {
        userService.deleteUser(IdUser);
    }


    @PutMapping("/update-User/{id}")
    public ResponseEntity<User> updateUser2(@PathVariable(value = "id") Long id, @RequestBody User user) {
        User updatedUser = userService.updateUser2(id, user);
        if(updatedUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/update-User")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(user);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            // You can further split this to give more granular error information
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
}
