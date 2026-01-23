package io.cx.ml.cli.commands;

import io.cx.ml.cli.dto.TokenResponse;
import io.cx.ml.cli.services.TokenService;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.Console;
import java.util.Optional;

@Command(name = "login", description = "Authenticate with Keycloak")
public class LoginCommand implements Runnable {

    @Inject
    TokenService tokenService;

    @Option(names = {"-u", "--username"}, description = "Username")
    String username;

    @Option(names = {"-p", "--password"}, description = "Password", arity = "0..1")
    char[] password;

    @Override
    public void run() {
        Console console = System.console();

        if (username == null) {
            if (console != null) {
                username = console.readLine("Username: ");
            } else {
                System.err.println("Error: Username is required.");
                return;
            }
        }

        if (password == null) {
            if (console != null) {
                password = console.readPassword("Password: ");
            } else {
                System.err.println("Error: Password is required.");
                return;
            }
        }

        // Вызов сервиса
        Optional<TokenResponse> login = tokenService.login(username, new String(password));

        // Безопасная проверка
        if (login.isPresent()) {
            System.out.println("Login successful. Access Token: " + login.get().getAccessToken().substring(0, 10) + "...");
        } else {
            System.err.println("Login failed. Check credentials or server availability.");
            System.exit(1); // Возвращаем код ошибки
        }
    }
}