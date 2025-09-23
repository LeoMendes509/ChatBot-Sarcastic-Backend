package com.leonardo.chatbot.test;

import com.leonardo.chatbot.model.User;
import com.leonardo.chatbot.service.ChatService;
import com.leonardo.chatbot.service.ChatHistoryService;
import com.leonardo.chatbot.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.Console;
import java.util.Scanner;

@SpringBootApplication(scanBasePackages = "com.leonardo.chatbot")
public class ConsoleUserTest {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ConsoleUserTest.class, args);
        UserService userService = context.getBean(UserService.class);
        ChatService chatService = context.getBean(ChatService.class);
        ChatHistoryService chatHistoryService = context.getBean(ChatHistoryService.class);

        Scanner scanner = new Scanner(System.in);
        Console console = System.console();

        boolean running = true;
        User loggedUser = null;

        while (running) {
            System.out.println("\n==============================");
            System.out.println("🤖 Chatbot Sarcastic - Menu");
            System.out.println("==============================");
            System.out.println("1 - Register (Criar usuário)");
            System.out.println("2 - Login (Entrar)");
            System.out.println("3 - Chat (Bater papo com sarcasmo 😏)");
            System.out.println("4 - Delete User (Apagar conta)");
            System.out.println("5 - Delete Chat Session (Apagar sessão de chat)");
            System.out.println("6 - Delete All Chat Sessions (Apagar todas as sessões)");
            System.out.println("7 - Exit (Sair)");
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
                    newUser.setPasswordHash(password); // encode será feito no UserService
                    newUser.setName(name);
                    newUser.setEmail(email);
                    newUser.setAge(age);
                    newUser.setEmailVerified(true);

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

                    try {
                        String token = userService.login(loginUsername, loginPassword);
                        loggedUser = userService.getUserByUserName(loginUsername).get();
                        System.out.println("✅ Login successful! Welcome, " + loggedUser.getName() + " 😎");
                    } catch (IllegalArgumentException e) {
                        System.out.println("❌ " + e.getMessage());
                    }
                    break;

                case "3": // CHAT
                    if (loggedUser == null) {
                        System.out.println("⚠️ You must login first!");
                        break;
                    }

                    String language;
                    while (true) {
                        System.out.print("Choose language / Escolha idioma (pt/en): ");
                        language = scanner.nextLine().trim().toLowerCase();
                        if (language.equals("pt") || language.equals("en")) break;
                        System.out.println("⚠️ Invalid language! Use 'pt' or 'en'.");
                    }

                    System.out.print("Enter session name / Nome da sessão: ");
                    String sessionName = scanner.nextLine().trim();
                    if (sessionName.isBlank()) sessionName = "New Chat";

                    System.out.println("💬 Chat started! Type 'exit' to leave / digite 'exit' para sair.");
                    while (true) {
                        System.out.print("> ");
                        String message = scanner.nextLine();
                        if (message.equalsIgnoreCase("exit")) {
                            // Pergunta se quer salvar histórico
                            System.out.print(language.equals("pt") ?
                                    "Deseja salvar esta conversa? (s/n): " :
                                    "Do you want to save this conversation? (y/n): ");
                            String save = scanner.nextLine().trim().toLowerCase();
                            if (save.equals("s") || save.equals("y")) {
                                System.out.println(language.equals("pt") ?
                                        "✅ Conversa salva!" : "✅ Conversation saved!");
                                // Histórico já foi salvo automaticamente pelo ChatService
                            }
                            break;
                        }

                        // Chama o ChatService para gerar resposta e salvar automaticamente
                        String response = chatService.getChatbotResponse(loggedUser, message, language, sessionName);
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
                    break;

                case "5": // DELETE CHAT SESSION
                    if (loggedUser == null) {
                        System.out.println("⚠️ You must login first!");
                        break;
                    }
                    System.out.print("Enter session name to delete: ");
                    String delSession = scanner.nextLine().trim();
                    if (!delSession.isBlank()) {
                        chatHistoryService.deleteSession(loggedUser, delSession);
                        System.out.println("✅ Session '" + delSession + "' deleted successfully!");
                    } else {
                        System.out.println("⚠️ Session name cannot be blank.");
                    }
                    break;

                case "6": // DELETE ALL CHAT SESSIONS
                    if (loggedUser == null) {
                        System.out.println("⚠️ You must login first!");
                        break;
                    }
                    chatHistoryService.deleteAllSessions(loggedUser);
                    System.out.println("✅ All chat sessions deleted successfully!");
                    break;

                case "7": // EXIT
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
