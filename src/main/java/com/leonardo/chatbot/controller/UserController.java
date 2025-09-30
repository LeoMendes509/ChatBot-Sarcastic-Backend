package com.leonardo.chatbot.controller;

import com.leonardo.chatbot.model.User;
import com.leonardo.chatbot.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "User", description = "Endpoints for user management")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 🔹 Deletar conta
    @DeleteMapping("/delete")
    @Operation(summary = "Delete user account", description = "Deletes the authenticated user's account")
    public ResponseEntity<?> deleteUser(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization").substring(7);
            User user = userService.getUserFromToken(token);
            userService.deleteUser(user);
            return ResponseEntity.ok("✅ User deleted successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(401).body("⚠️ " + e.getMessage());
        }
    }

    // 🔹 Buscar informações do usuário autenticado
    @GetMapping("/me")
    @Operation(summary = "Get user info", description = "Retrieve information of the authenticated user")
    public ResponseEntity<?> getUserInfo(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization").substring(7);
            User user = userService.getUserFromToken(token);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("⚠️ " + e.getMessage());
        }
    }
}
