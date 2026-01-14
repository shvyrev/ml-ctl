package io.cx.ml.cli.clients;

import io.cx.ml.cli.dto.StoreModelRequest;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/api/v1/service")
@RegisterRestClient(configKey = "artifact-api")
@RegisterClientHeaders(AuthHeaderFactory.class)
@RegisterProvider(LoggingResponseFilter.class)
public interface ArtifactStoreClient {

    /**
     * Вызов метода подготовки модели для инференса
     */
    @POST
    @Path("/store/model")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Uni<Void> storeModel(StoreModelRequest request);
}