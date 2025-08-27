package com.leonardo.chatbot.test;

import com.leonardo.chatbot.model.User;
import com.leonardo.chatbot.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.Console;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Scanner;

@SpringBootApplication(scanBasePackages = "com.leonardo.chatbot")
public class ConsoleUserTest {

    public static void main(String[] args) {
        // Initialize Spring Context
        ApplicationContext context = SpringApplication.run(ConsoleUserTest.class, args);

        UserService userService = context.getBean(UserService.class);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        Scanner scanner = new Scanner(System.in);
        Console console = System.console(); // for password masking
        boolean running = true;

        while (running) {
            System.out.println("\nChoose an option:");
            System.out.println("1 - Register User");
            System.out.println("2 - Login");
            System.out.println("3 - Delete User");
            System.out.println("4 - Exit");
            System.out.print("> ");
            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    System.out.print("Enter username: ");
                    String username = scanner.nextLine();

                    if (userService.existsByUsername(username)) {
                        System.out.println("❌ Username already exists!");
                        break;
                    }

                    String password;
                    if (console != null) {
                        char[] pwdArray = console.readPassword("Enter password: ");
                        password = new String(pwdArray);
                    } else {
                        System.out.print("Enter password: ");
                        password = scanner.nextLine();
                    }

                    System.out.print("Enter full name: ");
                    String name = scanner.nextLine();

                    System.out.print("Enter email: ");
                    String email = scanner.nextLine();

                    System.out.print("Enter age: ");
                    int age = Integer.parseInt(scanner.nextLine());

                    // Create new user with all required fields
                    User newUser = new User();
                    newUser.setUsername(username);
                    newUser.setPasswordHash(password); // will be encoded in UserService
                    newUser.setName(name);
                    newUser.setEmail(email);
                    newUser.setAge(age);
                    newUser.setCreatedAt(LocalDateTime.now());

                    userService.createUser(newUser);
                    System.out.println("✅ User successfully registered!");
                    break;

                case "2":
                    System.out.print("Enter username: ");
                    String loginUsername = scanner.nextLine();

                    String loginPassword;
                    if (console != null) {
                        char[] loginPwdArray = console.readPassword("Enter password: ");
                        loginPassword = new String(loginPwdArray);
                    } else {
                        System.out.print("Enter password: ");
                        loginPassword = scanner.nextLine();
                    }

                    Optional<User> userOpt = userService.getUserByUserName(loginUsername);
                    if (userOpt.isPresent()) {
                        User user = userOpt.get();
                        if (passwordEncoder.matches(loginPassword, user.getPasswordHash())) {
                            System.out.println("✅ Login successful!");
                        } else {
                            System.out.println("❌ Invalid password.");
                        }
                    } else {
                        System.out.println("❌ User not found.");
                    }
                    break;

                case "3":
                    System.out.print("Enter username to delete: ");
                    String deleteUsername = scanner.nextLine();

                    Optional<User> deleteOpt = userService.getUserByUserName(deleteUsername);
                    if (deleteOpt.isPresent()) {
                        userService.deleteUser(deleteOpt.get());
                        System.out.println("✅ User deleted successfully!");
                    } else {
                        System.out.println("❌ User not found.");
                    }
                    break;

                case "4":
                    running = false;
                    System.out.println("👋 Exiting...");
                    break;

                default:
                    System.out.println("⚠️ Invalid option!");
            }
        }

        scanner.close();
    }
}


