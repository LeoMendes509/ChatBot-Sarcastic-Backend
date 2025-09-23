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

    // Cria usuário e codifica a senha
    public User createUser(User user) {
        if (existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already registered.");
        }
        if (existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already registered.");
        }

        // Centraliza codificação de senha aqui
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

        user.setEmailVerified(true); // já deixa como verificado
        user.setEmailVerificationToken(null);

        return userRepository.save(user);
    }

    // Login com username + senha
    public String login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (!passwordEncoder.matches(password, user.getPasswordHash())) {
                throw new IllegalArgumentException("Incorrect password.");
            }

            return securityJWT.generateToken(username);
        }
        throw new IllegalArgumentException("User not found.");
    }

    // Outros métodos permanecem iguais
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUserName(String username) {
        return userRepository.findByUsername(username);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public User getUserFromToken(String token) {
        String username = securityJWT.getUsernameFromToken(token);

        if (username == null) {
            throw new RuntimeException("Invalid or expired token");
        }

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
