package com.leonardo.chatbot.controller;

import com.leonardo.chatbot.dto.ChatRequest;
import com.leonardo.chatbot.dto.ChatResponse;
import com.leonardo.chatbot.dto.ChatHistoryDTO;
import com.leonardo.chatbot.model.User;
import com.leonardo.chatbot.security.SecurityJWT;
import com.leonardo.chatbot.service.ChatService;
import com.leonardo.chatbot.service.ChatHistoryService;
import com.leonardo.chatbot.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;
    private final SecurityJWT securityJWT;
    private final ChatHistoryService chatHistoryService;

    public ChatController(ChatService chatService,
                          UserService userService,
                          SecurityJWT securityJWT,
                          ChatHistoryService chatHistoryService) {
        this.chatService = chatService;
        this.userService = userService;
        this.securityJWT = securityJWT;
        this.chatHistoryService = chatHistoryService;
    }

    // 🔹 Envia mensagem para o bot e salva no histórico de sessão
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody ChatRequest chatRequest,
                                         @RequestHeader("Authorization") String authHeader,
                                         @RequestParam(required = false) String sessionName) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String username = securityJWT.getUsernameFromToken(token);

            Optional<User> userOpt = userService.getUserByUserName(username);
            if (userOpt.isEmpty() || !userOpt.get().isEmailVerified()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Email not verified. Confirm your email to access the chat.");
            }

            User user = userOpt.get();

            String language = chatRequest.getLanguage();
            if (language == null || (!language.equals("pt") && !language.equals("en"))) {
                language = "en";
            }

            if (sessionName == null || sessionName.isBlank()) {
                sessionName = "New Chat";
            }

            String botResponse = chatService.getChatbotResponse(user, chatRequest.getMessage(), language, sessionName);

            return ResponseEntity.ok(new ChatResponse(botResponse));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired token.");
        }
    }

    // 🔹 Retorna as sessões recentes do usuário (últimas 48h)
    @GetMapping("/sessions")
    public ResponseEntity<?> getRecentSessions(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String username = securityJWT.getUsernameFromToken(token);

            Optional<User> userOpt = userService.getUserByUserName(username);
            if (userOpt.isEmpty() || !userOpt.get().isEmailVerified()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Email not verified.");
            }

            User user = userOpt.get();
            List<String> sessions = chatHistoryService.getRecentSessions(user);

            return ResponseEntity.ok(sessions);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired token.");
        }
    }

    // 🔹 Retorna o histórico completo de uma sessão
    @GetMapping("/session/history")
    public ResponseEntity<?> getSessionHistory(@RequestHeader("Authorization") String authHeader,
                                               @RequestParam String sessionName) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String username = securityJWT.getUsernameFromToken(token);

            Optional<User> userOpt = userService.getUserByUserName(username);
            if (userOpt.isEmpty() || !userOpt.get().isEmailVerified()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Email not verified.");
            }

            User user = userOpt.get();
            List<ChatHistoryDTO> history = chatHistoryService.getHistoryBySession(user, sessionName);

            return ResponseEntity.ok(history);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired token.");
        }
    }

    // 🔹 Apaga uma sessão específica
    @DeleteMapping("/session/{sessionName}")
    public ResponseEntity<?> deleteChatSession(@RequestHeader("Authorization") String authHeader,
                                               @PathVariable String sessionName) {
        try {
            String token = authHeader.replace("Bearer ", "");
            User user = userService.getUserFromToken(token);

            chatHistoryService.deleteSession(user, sessionName);
            return ResponseEntity.ok("✅ Chat session '" + sessionName + "' deleted successfully!");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired token.");
        }
    }

    // 🔹 Apaga todas as sessões do usuário
    @DeleteMapping("/all")
    public ResponseEntity<?> deleteAllChats(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            User user = userService.getUserFromToken(token);

            chatHistoryService.deleteAllSessions(user);
            return ResponseEntity.ok("✅ All chat sessions deleted successfully!");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired token.");
        }
    }
}

