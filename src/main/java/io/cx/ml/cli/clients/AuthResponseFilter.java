package io.cx.ml.cli.clients;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class AuthResponseFilter implements ClientResponseFilter {

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        int status = responseContext.getStatus();

        // 302 часто используется серверами для редиректа на форму логина OIDC
        // 401 — классический признак того, что токен не принят
        if (status == 302 || status == 401) {
            System.err.println("\n" + "=".repeat(50));
            System.err.println("[!] ОШИБКА АВТОРИЗАЦИИ (Status: " + status + ")");
            System.err.println("Ваша сессия истекла или отсутствует.");
            System.err.println("Пожалуйста, выполните вход заново:");
            System.err.println("\n   ./ml-cli login");
            System.err.println("=".repeat(50) + "\n");
        }
    }
}