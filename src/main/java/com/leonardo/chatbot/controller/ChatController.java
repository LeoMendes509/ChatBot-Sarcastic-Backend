package com.leonardo.chatbot.controller;

import com.leonardo.chatbot.dto.ChatRequest;
import com.leonardo.chatbot.dto.ChatResponse;
import com.leonardo.chatbot.model.User;
import com.leonardo.chatbot.security.SecurityJWT;
import com.leonardo.chatbot.service.ChatService;
import com.leonardo.chatbot.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;
    private final SecurityJWT securityJWT;

    public ChatController(ChatService chatService, UserService userService, SecurityJWT securityJWT) {
        this.chatService = chatService;
        this.userService = userService;
        this.securityJWT = securityJWT; // permite acessar métodos de JWT
    }

    @PostMapping
    public ResponseEntity<?> sendMessage(@RequestBody ChatRequest chatRequest,
                                         @RequestHeader("Authorization") String authHeader) {
        try {
            // remove "Bearer " do token
            String token = authHeader.replace("Bearer ", "");

            // extrai username do token
            String username = securityJWT.getUsernameFromToken(token);

            // encontra o usuário no banco
            Optional<User> userOpt = userService.getUserByUserName(username);

            // verifica se usuário existe e email está verificado
            if (userOpt.isEmpty() || !userOpt.get().isEmailVerified()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Email não verificado. Confirme seu email para acessar o chat.");
            }

            // define idioma enviado pelo front-end (pt ou en)
            String language = chatRequest.getLanguage();
            if (language == null || (!language.equals("pt") && !language.equals("en"))) {
                language = "pt"; // fallback padrão
            }

            // gera resposta do chatbot
            String botResponse = chatService.getChatbotResponse(chatRequest.getMessage(), language);
            return ResponseEntity.ok(new ChatResponse(botResponse));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token inválido ou expirado.");
        }
    }
}
