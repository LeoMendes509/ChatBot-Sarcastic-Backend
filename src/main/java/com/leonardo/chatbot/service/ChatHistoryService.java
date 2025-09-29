package com.leonardo.chatbot.service;

import com.leonardo.chatbot.dto.ChatHistoryDTO;
import com.leonardo.chatbot.model.ChatHistory;
import com.leonardo.chatbot.model.User;
import com.leonardo.chatbot.repository.ChatHistoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatHistoryService {

    private final ChatHistoryRepository historyRepository;

    public ChatHistoryService(ChatHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    // 🔹 Salva mensagem no histórico
    public ChatHistory saveMessage(User user, String sessionName, String message, String response, String language) {
        ChatHistory history = new ChatHistory();
        history.setUser(user);
        history.setSessionName(sessionName);
        history.setMessage(message);
        history.setResponse(response);
        history.setLanguage(language);
        history.setTimestamp(LocalDateTime.now());
        return historyRepository.save(history);
    }

    // 🔹 Retorna histórico de uma sessão (últimas 48h)
    public List<ChatHistoryDTO> getHistoryBySession(User user, String sessionName) {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(48);
        return historyRepository.findByUserAndSessionNameAndTimestampAfter(user, sessionName, cutoff)
                .stream()
                .map(h -> new ChatHistoryDTO(
                        h.getSessionName(),
                        h.getMessage(),
                        h.getResponse(),
                        h.getLanguage(),
                        h.getTimestamp()
                ))
                .collect(Collectors.toList());
    }

    // 🔹 Lista sessões recentes do usuário (últimas 48h)
    public List<String> getRecentSessions(User user) {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(48);
        return historyRepository.findByUserAndTimestampAfter(user, cutoff)
                .stream()
                .map(ChatHistory::getSessionName)
                .distinct()
                .toList();
    }

    // 🔹 Deleta sessão específica
    @Transactional
    public void deleteSession(User user, String sessionName) {
        historyRepository.deleteByUserAndSessionName(user, sessionName);
    }

    // 🔹 Deleta todas as sessões
    @Transactional
    public void deleteAllSessions(User user) {
        historyRepository.deleteByUser(user);
    }
}

