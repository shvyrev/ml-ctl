package io.cx.ml.cli.clients;

import io.cx.ml.cli.dto.StoreModelRequest;
import io.cx.ml.cli.dto.UserAllContentResponse;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
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
     * Удалить весь контент конкретного пользователя
     */
    @DELETE
    @Path("/content/{user}")
    @Produces(MediaType.APPLICATION_JSON)
    Uni<Void> deleteUserContent(@PathParam("user") String user);

    /**
     * Получить весь контент конкретного пользователя
     */
    @GET
    @Path("/content/{user}")
    @Produces(MediaType.APPLICATION_JSON)
    Uni<UserAllContentResponse> getUserContent(@PathParam("user") String user);

    /**
     * Вызов метода подготовки модели для инференса
     */
    @POST
    @Path("/store/model")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Uni<Void> storeModel(StoreModelRequest request);
}