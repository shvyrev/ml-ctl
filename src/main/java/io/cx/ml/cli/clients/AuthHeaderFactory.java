package io.cx.ml.cli.clients;

import io.cx.ml.cli.dao.ConfigStore;
import io.cx.ml.cli.services.TokenService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;

@Slf4j
@ApplicationScoped
public class AuthHeaderFactory implements ClientHeadersFactory {

    @Inject
    TokenService tokenService;

    @Override
    public MultivaluedMap<String, String> update(MultivaluedMap<String, String> incoming,
                                                 MultivaluedMap<String, String> outgoing) {
        MultivaluedMap<String, String> result = new MultivaluedHashMap<>();

        // Метод getValidAccessToken сам проверит срок и сделает refresh если нужно
        tokenService.getValidAccessToken().ifPresentOrElse(
                token -> result.add("Authorization", "Bearer " + token),
                () -> log.warn("Не удалось получить валидный токен. Запрос уйдет без авторизации.")
        );

        return result;
    }
}