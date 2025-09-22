package com.leonardo.chatbot.controller;

import com.leonardo.chatbot.dto.LoginRequest;
import com.leonardo.chatbot.model.User;
import com.leonardo.chatbot.security.SecurityJWT;
import com.leonardo.chatbot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final SecurityJWT securityJWT ;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder, SecurityJWT securityJWT) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.securityJWT = securityJWT;
    }


//    @Autowired
//    private EmailService emailService;
//
//    @PostMapping("/register")
//    public ResponseEntity<?> registerUser(@RequestBody User user) {
//        if (userService.existsByUsername(user.getUsername())) {
//            return ResponseEntity.badRequest().body("Username already exists!");
//        }
//
//        User savedUser = userService.createUserWithEmailVerification(user);
//
//        // Monta link de verificação com token
//        String link = "http://localhost:5173/verify-email?token=" + savedUser.getEmailVerificationToken();
//        String emailText = "Hello " + savedUser.getName() + ",\n\n"
//                + "Please verify your email using this link:\n" + link;
//
//        emailService.sendEmail(savedUser.getEmail(), "Verify your email", emailText);
//
//        savedUser.setPasswordHash(null); // não enviar senha de volta
//        return ResponseEntity.ok("User registered! Please check your email to verify.");
//    }




    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOpt = userService.getUserByUserName(loginRequest.getUsername());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {

                String token = securityJWT.generateToken(user.getUsername());

                return ResponseEntity.ok(
                       Map.of(
                               "username", user.getUsername(),
                               "token", token
                       )
               );

            } else {
                return ResponseEntity.status(401).body("Invalid password.");
            }
        } else {
            return ResponseEntity.status(404).body("User not found.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(u -> {
            u.setPasswordHash(null);
            return ResponseEntity.ok(u);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(400).body("Missing or invalid Authorization header.");
            }

            String token = authHeader.substring(7);

            if (!securityJWT.validateJwtToken(token)) {
                return ResponseEntity.status(401).body("Invalid or expired token.");
            }

            String username = securityJWT.getUsernameFromToken(token);

            Optional<User> userOpt = userService.getUserByUserName(username);

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setPasswordHash(null);
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(404).body("User not found.");
            }

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving user: " + e.getMessage());
        }
    }
//
//    @GetMapping("/verify-email")
//    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
//        Optional<User> userOpt = userService.getUserByVerificationToken(token);
//        if (userOpt.isPresent()) {
//            User user = userOpt.get();
//            user.setEmailVerified(true);
//            user.setEmailVerificationToken(null); // remove token após verificação
//            userService.saveUser(user);
//            return ResponseEntity.ok("Email verified successfully!");
//        } else {
//            return ResponseEntity.status(400).body("Invalid or expired token.");
//        }
//    }
//


}


