package io.cx.ml.cli.clients;

import io.cx.ml.cli.dao.ConfigStore;
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
    ConfigStore configStore;

    @Override
    public MultivaluedMap<String, String> update(MultivaluedMap<String, String> incoming,
                                                 MultivaluedMap<String, String> outgoing) {
        MultivaluedMap<String, String> result = new MultivaluedHashMap<>();
        try {
            String token = configStore.resolveAccessToken();
            result.add("Authorization", "Bearer " + token);
        } catch (Exception e) {
            log.warn("No token found", e);
            // Если токена нет, запрос уйдет без заголовка (сервер вернет 401)
            // Либо можно логировать здесь необходимость логина
        }
        return result;
    }
}