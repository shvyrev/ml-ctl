package io.cx.ml.cli.clients;

import io.cx.ml.cli.dto.*;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;
import java.util.UUID;

@Path("/api/v1/model")
@RegisterRestClient(configKey = "serving-api")
@RegisterProvider(AuthResponseFilter.class)
@RegisterClientHeaders(AuthHeaderFactory.class)
@RegisterProvider(LoggingResponseFilter.class)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ModelServingClient {

    @GET
    @Path("/triton/frameworks")
    Uni<List<TritonSupportFrameworks>> getFrameworks();

    @GET
    Uni<ListResponse<ModelVersionResponseExtended>> listModels(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size
    );

    @GET
    @Path("/{model-id}")
    Uni<ModelVersionResponseExtended> getModel(@PathParam("model-id") UUID modelId);

    @POST
    Uni<ModelVersionResponseExtended> createModel(ModelCreateRequestExtended request);

    @PUT
    @Path("/{model-id}")
    Uni<ModelVersionResponseExtended> updateModel(
            @PathParam("model-id") UUID modelId,
            ModelUpdateRequestExtended request
    );

    @DELETE
    @Path("/{model-id}")
    Uni<Void> deleteModel(@PathParam("model-id") UUID modelId);

    @GET
    @Path("/statuses")
    Uni<ListResponse<ModelStatus>> getStatuses(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size
    );
}