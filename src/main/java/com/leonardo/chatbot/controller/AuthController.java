package com.leonardo.chatbot.controller;

import com.leonardo.chatbot.dto.LoginRequestDTO;
import com.leonardo.chatbot.dto.RegisterRequestDTO;
import com.leonardo.chatbot.model.User;
import com.leonardo.chatbot.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth" , description = "Authentication endpoints : register and login .")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // 🔹 Registro
    @PostMapping("/register")
    @Operation(
            summary = "Register user",
            description = "Creates a new user with username , password , name , email and age .",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "DTO containing username , password , name , email and age .",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RegisterRequestDTO.class),
                            examples = @ExampleObject(value = "{ \"username\" : \"leo\" , \"password\" : \"123456\" , " +
                                    "\"name\" : , \"Leonardo\", \"email\" : \"leonardo@email.com\" , \"age\" : 24 }")
                    )
            )
    )

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
    @Operation(
            summary = "User login",
            description = "Authenticates a user and returns a JWT token",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "DTO containing username and password" ,
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = LoginRequestDTO.class),
                            examples = @ExampleObject(value = "{ \"username\": \"leo\", \"password\": \"123456\" }")
                    )
                    )
            )

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
