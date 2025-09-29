package com.leonardo.chatbot.service;

import com.leonardo.chatbot.model.User;
import com.leonardo.chatbot.repository.UserRepository;
import com.leonardo.chatbot.security.SecurityJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityJWT securityJWT;

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       SecurityJWT securityJWT) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityJWT = securityJWT;
    }

    // 🔹 Cria usuário e codifica a senha
    public User createUser(User user) {
        if (existsByEmail(user.getEmail()))
            throw new IllegalArgumentException("⚠️ Email already registered !");
        if (existsByUsername(user.getUsername()))
            throw new IllegalArgumentException("⚠️ Username already exists !");

        // 🔹 Codifica a senha pura
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        user.setEmailVerified(true); // já deixa como verificado
        user.setEmailVerificationToken(null);

        return userRepository.save(user);
    }

    // 🔹 Login com username + senha
    public String login(String username, String rawPassword) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
                throw new IllegalArgumentException("❌ Incorrect password.");
            }
            return securityJWT.generateToken(username);
        }
        throw new IllegalArgumentException("❌ User not found.");
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUserName(String username) {
        return userRepository.findByUsername(username);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // 🔹 DELETE: agora apaga automaticamente as mensagens do usuário (cascade)
    public void deleteUser(User user) {
        userRepository.delete(user); // <--- ALTERAÇÃO PRINCIPAL: não precisa deletar mensagens manualmente
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    // 🔹 Recupera usuário a partir do token JWT
    public User getUserFromToken(String token) {
        String username = securityJWT.getUsernameFromToken(token);
        if (username == null)
            throw new RuntimeException("⚠️ Invalid or expired token.");

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("❌ User not found."));
    }
}
