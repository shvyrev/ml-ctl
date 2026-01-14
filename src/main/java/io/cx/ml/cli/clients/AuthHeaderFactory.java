package io.cx.ml.cli.clients;

import io.cx.ml.cli.services.TokenManager;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;

@ApplicationScoped
public class AuthHeaderFactory implements ClientHeadersFactory {

    @Inject
    TokenManager tokenManager;

    @Override
    public MultivaluedMap<String, String> update(MultivaluedMap<String, String> incoming,
                                                 MultivaluedMap<String, String> outgoing) {
        MultivaluedMap<String, String> result = new MultivaluedHashMap<>();
        tokenManager.getValidAccessToken().ifPresent(token ->
                result.add("Authorization", "Bearer " + token)
        );
        return result;
    }
}