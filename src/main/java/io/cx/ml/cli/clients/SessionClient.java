package io.cx.ml.cli.clients;

import io.cx.ml.cli.dto.ListResponse;
import io.cx.ml.cli.dto.UploadSessionRequest;
import io.cx.ml.cli.dto.UploadSessionResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import java.util.UUID;

@Path("/api/v1/sessions") // BASE_SESSIONS_PATH
@RegisterRestClient(configKey = "artifact-api")
@RegisterClientHeaders(AuthHeaderFactory.class)
@RegisterProvider(LoggingResponseFilter.class)
public interface SessionClient {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    UploadSessionResponse createSession(UploadSessionRequest request);

    @PATCH
    @Path("/{sessionId}")
    @Consumes("application/offset+octet-stream")
    @Produces(MediaType.APPLICATION_JSON)
    Response uploadChunk(
            @PathParam("sessionId") UUID sessionId,
            @HeaderParam("Upload-Offset") long offset,
            byte[] chunk
    );

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    ListResponse<UploadSessionResponse> getSessions(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("100") int size
    );
}