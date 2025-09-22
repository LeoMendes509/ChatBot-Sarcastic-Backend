package com.leonardo.chatbot.test;

import com.leonardo.chatbot.model.User;
import com.leonardo.chatbot.service.ChatService;
import com.leonardo.chatbot.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.Console;
import java.util.Optional;
import java.util.Scanner;

@SpringBootApplication(scanBasePackages = "com.leonardo.chatbot")
public class ConsoleUserTest {

    public static void main(String[] args) {
        // 🔹 Inicializa Spring Boot e obtém os beans
        ApplicationContext context = SpringApplication.run(ConsoleUserTest.class, args);
        UserService userService = context.getBean(UserService.class);
        ChatService chatService = context.getBean(ChatService.class);

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Scanner scanner = new Scanner(System.in);
        Console console = System.console();

        boolean running = true;
        String loggedToken = null;
        User loggedUser = null;

        // 🔹 Loop principal do menu
        while (running) {
            System.out.println("\n==============================");
            System.out.println("🤖 Chatbot Sarcastic - Menu");
            System.out.println("==============================");
            System.out.println("1 - Register (Criar usuário)");
            System.out.println("2 - Login (Entrar)");
            System.out.println("3 - Chat (Bater papo com sarcasmo 😏)");
            System.out.println("4 - Delete User (Apagar conta)");
            System.out.println("5 - Exit (Sair)");
            System.out.print("> ");
            String option = scanner.nextLine().trim();

            switch (option) {
                case "1": // REGISTER
                    System.out.print("Enter username: ");
                    String username = scanner.nextLine();

                    if (userService.existsByUsername(username)) {
                        System.out.println("❌ Username already exists!");
                        break;
                    }

                    String password;
                    if (console != null) {
                        password = new String(console.readPassword("Enter password: "));
                    } else {
                        System.out.print("Enter password: ");
                        password = scanner.nextLine();
                    }

                    System.out.print("Enter full name: ");
                    String name = scanner.nextLine();

                    System.out.print("Enter email: ");
                    String email = scanner.nextLine();

                    if (userService.existsByEmail(email)) {
                        System.out.println("❌ Email already registered!");
                        break;
                    }

                    System.out.print("Enter age: ");
                    int age;
                    try {
                        age = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("⚠️ Invalid age. Registration canceled.");
                        break;
                    }

                    User newUser = new User();
                    newUser.setUsername(username);
                    newUser.setPasswordHash(password);
                    newUser.setName(name);
                    newUser.setEmail(email);
                    newUser.setAge(age);

                    userService.createUser(newUser);
                    System.out.println("✅ User registered successfully! You can login now.");
                    break;

                case "2": // LOGIN
                    System.out.print("Enter username: ");
                    String loginUsername = scanner.nextLine();

                    String loginPassword;
                    if (console != null) {
                        loginPassword = new String(console.readPassword("Enter password: "));
                    } else {
                        System.out.print("Enter password: ");
                        loginPassword = scanner.nextLine();
                    }

                    Optional<User> userOpt = userService.getUserByUserName(loginUsername);
                    if (userOpt.isPresent()) {
                        User user = userOpt.get();
                        if (passwordEncoder.matches(loginPassword, user.getPasswordHash())) {
                            loggedToken = userService.login(loginUsername, loginPassword);
                            loggedUser = user;
                            System.out.println("✅ Login successful! Welcome, " + user.getName() + " 😎");
                        } else {
                            System.out.println("❌ Invalid password.");
                        }
                    } else {
                        System.out.println("❌ User not found.");
                    }
                    break;

                case "3": // CHAT
                    if (loggedToken == null || loggedUser == null) {
                        System.out.println("⚠️ You must login first!");
                        break;
                    }

                    // Escolha do idioma antes do chat
                    String language = null;
                    while (true) {
                        System.out.print("Choose language / Escolha idioma (pt/en): ");
                        language = scanner.nextLine().trim().toLowerCase();
                        if (language.equals("pt") || language.equals("en")) break;
                        System.out.println("⚠️ Invalid language! Use 'pt' or 'en'.");
                    }

                    System.out.println("💬 Chat started! Type 'exit' to leave / digite 'exit' para sair.");
                    while (true) {
                        System.out.print("> ");
                        String message = scanner.nextLine();
                        if (message.equalsIgnoreCase("exit")) break;

                        // Enviando o idioma escolhido para o ChatService
                        String response = chatService.getChatbotResponse(message, language);
                        System.out.println("🤖 " + response);
                    }
                    break;

                case "4": // DELETE USER
                    if (loggedUser == null) {
                        System.out.println("⚠️ You must login first to delete your account!");
                        break;
                    }

                    userService.deleteUser(loggedUser);
                    System.out.println("✅ User deleted successfully!");
                    loggedUser = null;
                    loggedToken = null;
                    break;

                case "5": // EXIT
                    running = false;
                    System.out.println("👋 Goodbye!");
                    break;

                default:
                    System.out.println("⚠️ Invalid option!");
            }
        }

        scanner.close();
    }
}
