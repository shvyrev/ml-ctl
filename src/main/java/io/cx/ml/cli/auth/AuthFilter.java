package io.cx.ml.cli.auth;

import io.cx.ml.cli.services.TokenService;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * Фильтр для автоматического добавления Bearer токена в заголовки REST клиента.
 * Использует TokenService для получения валидного access токена.
 */
@ApplicationScoped
@ClientHeaderParam(name = "Authorization", value = "{getAuthHeader}")
public class AuthFilter implements ClientRequestFilter {

    @Inject
    TokenService tokenService;

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        // Дополнительная логика, если нужно
        // Автоматическое добавление заголовка осуществляется через @ClientHeaderParam
    }

    /**
     * Метод, вызываемый аннотацией @ClientHeaderParam для получения значения заголовка.
     * @return строка "Bearer <token>" или выбрасывает исключение, если токен отсутствует.
     */
    public String getAuthHeader() {
        Optional<String> token = tokenService.getValidAccessToken();
        if (token.isEmpty()) {
            throw new RuntimeException("Not authenticated. Please run 'cx-ml-cli login'.");
        }
        return "Bearer " + token.get();
    }
}