package io.cx.ml.cli.clients;

import io.cx.ml.cli.dto.TokenResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.jboss.resteasy.reactive.RestForm;

@Path("/protocol/openid-connect/token")
@RegisterProvider(LoggingResponseFilter.class)
public interface KeycloakAuthClient {

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    TokenResponse fetchToken(
            @RestForm("grant_type") String grantType,
            @RestForm("client_id") String clientId,
            @RestForm("client_secret") String clientSecret,
            @RestForm("username") String username,
            @RestForm("password") String password,
            @RestForm("refresh_token") String refreshToken
    );
}