package io.cx.ml.cli.clients;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import java.util.UUID;

@Path("/api/v1/files")
@RegisterRestClient(configKey = "artifact-api")
@RegisterClientHeaders(AuthHeaderFactory.class)
@RegisterProvider(LoggingResponseFilter.class)
public interface FileClient {

    @DELETE
    @Path("/{fileId}")
    Uni<Void> deleteFile(@PathParam("fileId") UUID fileId);
}