package com.leonardo.chatbot.controller;

import com.leonardo.chatbot.dto.ChatHistoryDTO;
import com.leonardo.chatbot.dto.ChatRequest;
import com.leonardo.chatbot.dto.ChatResponse;
import com.leonardo.chatbot.model.User;
import com.leonardo.chatbot.service.ChatHistoryService;
import com.leonardo.chatbot.service.ChatService;
import com.leonardo.chatbot.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
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
    public ResponseEntity<?> sendMessage(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam String sessionName,
            @RequestParam(required = false) String language,
            @RequestBody ChatRequest chatRequest) {

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("⚠️ JWT token missing or invalid.");
            }

            String token = authHeader.substring(7);
            User user = userService.getUserFromToken(token);

            String userMessage = chatRequest.getMessage();

            String finalLanguage = chatRequest.getLanguage() != null
                    ? chatRequest.getLanguage()
                    : (language != null ? language : "auto");

            if (finalLanguage.equalsIgnoreCase("auto")) {
                finalLanguage = null;
            }

            log.info("[SEND] User: {} | Session: {} | Language: {} | Message: {}",
                    user.getUsername(),
                    sessionName,
                    (finalLanguage != null ? finalLanguage : "auto-detect"),
                    userMessage
            );

            String botResponse = chatService.getChatbotResponse(
                    user,
                    userMessage,
                    finalLanguage,
                    sessionName
            );

            log.info("[SEND] Bot response: {}", botResponse);

            return ResponseEntity.ok(new ChatResponse(botResponse));

        } catch (SecurityException e) {
            return ResponseEntity.status(401).body("⚠️ Access denied " + e.getMessage());

        } catch (Exception e) {
            System.out.println("[SEND][ERROR] " + e.getMessage());
            return ResponseEntity.status(500).body("\uD83D\uDCA5 Internal error while processing the message.");
        }
    }

    // 🔹 Salvar histórico do chat
    @PostMapping("/save-history")
    @Operation(summary = "Save chat history", description = "Save the current chat history for the user")
    public ResponseEntity<?> saveChatHistory(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam String sessionName,
            @RequestBody List<ChatHistoryDTO> messages) {

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("⚠️ JWT token missing or invalid.");
            }

            String token = authHeader.substring(7);
            User user = userService.getUserFromToken(token);

            // Salva cada mensagem do chat
            for (ChatHistoryDTO msg : messages) {
                historyService.saveMessage(
                        user,
                        sessionName,
                        msg.getMessage(),
                        msg.getResponse(),
                        msg.getLanguage()
                );
            }

            log.info("[SAVE HISTORY] Saved {} messages for user '{}' in session '{}'", messages.size(), user.getUsername(), sessionName);

            return ResponseEntity.ok("✅ Chat history saved successfully.");

        } catch (SecurityException e) {
            return ResponseEntity.status(401).body("⚠️ Access denied: " + e.getMessage());

        } catch (Exception e) {
            log.error("[SAVE HISTORY][ERROR] {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("⚠️ Internal error while saving chat history.");
        }
    }


    // 🔹 Recuperar histórico de uma sessão (últimas 48h)
    @GetMapping("/history")
    @Operation(summary = "Get chat history", description = "Retrieve chat history for a specific session")

    public ResponseEntity<?> getHistory(
                                        @RequestHeader(value = "Authorization", required = false) String authHeader,
                                        @RequestParam String sessionName) {

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("⚠️ JWT token missing or invalid.");
            }

            String token = authHeader.substring(7);

            User user = userService.getUserFromToken(token);

            log.info("[HISTORY] User : {} | Session : {} " , user.getUsername(),sessionName);

            List<ChatHistoryDTO> history = historyService.getHistoryBySession(user, sessionName);

            log.info("[HISTORY] Number of messages : {} " , history.size());

            return ResponseEntity.ok(history);

        } catch (SecurityException e) {
            return ResponseEntity.status(401).body("⚠️ Access denied: " + e.getMessage());

        } catch (Exception e ) {
            log.error("[HISTORY][ERROR] {}" , e.getMessage() , e);
            return ResponseEntity.status(500).body("⚠️ Internal error while retrieving chat history.");
        }
    }

    // 🔹 Deletar sessão específica
    @DeleteMapping("/session")
    @Operation(summary = "Delete session", description = "Delete a specific chat session")
    public ResponseEntity<?> deleteSession(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam String sessionName) {

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("⚠️ JWT token missing or invalid.");
            }

            String token = authHeader.substring(7);
            User user = userService.getUserFromToken(token);

            log.info("[DELETE SESSION] User: {} | Session: {}", user.getUsername(), sessionName);

            historyService.deleteSession(user, sessionName);

            log.info("[DELETE SESSION] Session '{}' successfully deleted for user {}", sessionName, user.getUsername());

            return ResponseEntity.ok("✅ Session '" + sessionName + "' successfully deleted !");

        } catch (SecurityException e) {
            return ResponseEntity.status(401).body("⚠️ Access denied: " + e.getMessage());

        } catch (Exception e) {
            log.error("[DELETE SESSION][ERROR] {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("⚠️ Internal error while deleting the session.");
        }
    }


    // 🔹 Deletar todas as sessões do usuário
    @DeleteMapping("/sessions")
    @Operation(summary = "Delete all sessions", description = "Delete all chat sessions for the authenticated user")
    public ResponseEntity<?> deleteAllSessions(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("⚠️ JWT token missing or invalid.");
            }

            String token = authHeader.substring(7);
            User user = userService.getUserFromToken(token);

            log.info("[DELETE ALL SESSIONS] User: {}", user.getUsername());

            historyService.deleteAllSessions(user);

            log.info("[DELETE ALL SESSIONS] All sessions successfully deleted for user {}", user.getUsername());

            return ResponseEntity.ok("✅ All sessions deleted successfully !");

        } catch (SecurityException e) {
            return ResponseEntity.status(401).body("⚠️ Access denied: " + e.getMessage());

        } catch (Exception e) {
            log.error("[DELETE ALL SESSIONS][ERROR] {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("⚠️ Internal error while deleting all sessions.");
        }
    }

}
