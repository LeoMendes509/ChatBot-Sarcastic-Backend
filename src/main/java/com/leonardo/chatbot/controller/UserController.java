package com.leonardo.chatbot.controller;

import com.leonardo.chatbot.model.User;
import com.leonardo.chatbot.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 🔹 Deletar conta
    @DeleteMapping("/delete")
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
