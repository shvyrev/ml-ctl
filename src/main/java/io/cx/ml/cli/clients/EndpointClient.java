package io.cx.ml.cli.clients;

import io.cx.ml.cli.dto.*;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;

import java.util.List;
import java.util.UUID;

@Path("/api/v1/endpoint")
@RegisterRestClient(configKey = "serving-api")
@RegisterClientHeaders(AuthHeaderFactory.class)
@RegisterProvider(LoggingResponseFilter.class)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface EndpointClient {

    @GET
    @Path("/gpu/flavors")
    Uni<List<GpuFlavorsDto>> getGpuFlavors();

    @GET
    @Path("/statuses")
    Uni<List<EndpointStatusDto>> getStatuses();

    @GET
    Uni<ListResponse<CreateEndpointResponseExtended>> listEndpoints(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size
    );

    @GET
    @Path("/{endpoint-id}")
    Uni<CreateEndpointResponseExtended> getEndpoint(@PathParam("endpoint-id") UUID endpointId);

    @POST
    Uni<CreateEndpointResponse> createEndpoint(EndpointCreateRequestExtended request);

    @PUT
    @Path("/{endpoint-id}")
    Uni<CreateEndpointResponse> updateEndpoint(
            @PathParam("endpoint-id") UUID endpointId,
            EndpointUpdateRequestExtended request
    );

    @DELETE
    @Path("/{endpoint-id}")
    Uni<Void> deleteEndpoint(@PathParam("endpoint-id") UUID endpointId);
}