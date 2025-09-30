package com.leonardo.chatbot.controller;

import com.leonardo.chatbot.dto.ChatHistoryDTO;
import com.leonardo.chatbot.model.User;
import com.leonardo.chatbot.service.ChatHistoryService;
import com.leonardo.chatbot.service.ChatService;
import com.leonardo.chatbot.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@Tag(name = "Chat", description = "Endpoints for sending messages and managing chat history")
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;
    private final ChatHistoryService historyService;

    public ChatController(ChatService chatService, UserService userService, ChatHistoryService historyService) {
        this.chatService = chatService;
        this.userService = userService;
        this.historyService = historyService;
    }

    // 🔹 Enviar mensagem para o chatbot e receber resposta
    @PostMapping("/send")
    @Operation(summary = "Send message", description = "Send a message to the chatbot and gets its response")
    public ResponseEntity<?> sendMessage(HttpServletRequest request,
                                         @RequestParam String sessionName,
                                         @RequestParam(required = false) String language,
                                         @RequestBody String message) {
        try {
            String token = request.getHeader("Authorization").substring(7);
            User user = userService.getUserFromToken(token);

            String response = chatService.getChatbotResponse(user, message, language, sessionName);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("⚠️ " + e.getMessage());
        }
    }

    // 🔹 Recuperar histórico de uma sessão (últimas 48h)
    @GetMapping("/history")
    @Operation(summary = "Get chat history", description = "Retrieve chat history for a specific session")

    public ResponseEntity<?> getHistory(HttpServletRequest request,
                                        @RequestParam String sessionName) {
        try {
            String token = request.getHeader("Authorization").substring(7);
            User user = userService.getUserFromToken(token);

            List<ChatHistoryDTO> history = historyService.getHistoryBySession(user, sessionName);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("⚠️ " + e.getMessage());
        }
    }

    // 🔹 Deletar sessão específica
    @DeleteMapping("/session")
    @Operation(summary = "Delete session", description = "Delete a specific chat session")
    public ResponseEntity<?> deleteSession(HttpServletRequest request,
                                           @RequestParam String sessionName) {
        try {
            String token = request.getHeader("Authorization").substring(7);
            User user = userService.getUserFromToken(token);

            historyService.deleteSession(user, sessionName);
            return ResponseEntity.ok("✅ Session '" + sessionName + "' successfully deleted !");
        } catch (Exception e) {
            return ResponseEntity.status(401).body("⚠️ " + e.getMessage());
        }
    }

    // 🔹 Deletar todas as sessões do usuário
    @DeleteMapping("/sessions")
    @Operation(summary = "Delete all sessions", description = "Delete all chat sessions for the authenticated user")
    public ResponseEntity<?> deleteAllSessions(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization").substring(7);
            User user = userService.getUserFromToken(token);

            historyService.deleteAllSessions(user);
            return ResponseEntity.ok("✅ All sessions deleted successfully !");
        } catch (Exception e) {
            return ResponseEntity.status(401).body("⚠️ " + e.getMessage());
        }
    }
}
