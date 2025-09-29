package com.leonardo.chatbot.controller;

import com.leonardo.chatbot.dto.LoginRequestDTO;
import com.leonardo.chatbot.dto.RegisterRequestDTO;
import com.leonardo.chatbot.model.User;
import com.leonardo.chatbot.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // 🔹 Registro
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequestDTO request) {
        try {
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPasswordHash(request.getPassword());
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setAge(request.getAge());

            User savedUser = userService.createUser(user);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "✅ User registered successfully",
                    "username", savedUser.getUsername(),
                    "email", savedUser.getEmail()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "⚠️ Something went wrong during registration"));
        }
    }

    // 🔹 Login
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequestDTO request) {
        try {
            String token = userService.login(request.getUsername(), request.getPassword());

            return ResponseEntity.ok(Map.of(
                    "message", "✅ Login successful",
                    "username", request.getUsername(),
                    "token", token
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "⚠️ Something went wrong during login"));
        }
    }
}
