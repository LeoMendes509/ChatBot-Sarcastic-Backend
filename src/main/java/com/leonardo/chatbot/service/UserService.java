package com.leonardo.chatbot.service;

import com.leonardo.chatbot.model.User;
import com.leonardo.chatbot.repository.UserRepository;
import com.leonardo.chatbot.security.SecurityJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

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

    // Cria usuário SEM envio de email
    public User createUser(User user) {
        if (existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already registered.");
        }
        if (existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already registered.");
        }

        // Token e verificação de email removidos (não usamos mais)
        user.setEmailVerified(true); // já deixa como verificado
        user.setEmailVerificationToken(null);
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

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

            // Como não temos verificação por e-mail, não precisa mais checar aqui
            return securityJWT.generateToken(username);
        }
        throw new IllegalArgumentException("User not found.");
    }

    // Busca usuário por ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Busca usuário por username
    public Optional<User> getUserByUserName(String username) {
        return userRepository.findByUsername(username);
    }

    // Salva usuário
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // Deleta usuário
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    // Verifica se username já existe
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    // Verifica se email já existe
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    // Extrai usuário a partir do token JWT
    public User getUserFromToken(String token) {
        String username = securityJWT.getUsernameFromToken(token);

        if (username == null) {
            throw new RuntimeException("Invalid or expired token");
        }

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
