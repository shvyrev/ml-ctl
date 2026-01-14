package io.cx.ml.cli.clients;

import io.cx.ml.cli.dto.FileInfoResponse;
import io.cx.ml.cli.dto.FolderCreateRequest;
import io.cx.ml.cli.dto.FolderInfoResponse;
import io.cx.ml.cli.dto.ListResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/api/v1/folders")
@RegisterRestClient(configKey = "artifact-api")
@RegisterClientHeaders(AuthHeaderFactory.class)
@RegisterProvider(LoggingResponseFilter.class)
public interface FolderClient {

    @GET
    @Path("/root") // GET /api/v1/folders
    FolderInfoResponse getRootFolder();

    // Метод для получения корневого контента (вызовется GET /api/v1/folders)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    ListResponse<FileInfoResponse> getRootContents(
            @QueryParam("page") int page,
            @QueryParam("size") int size
    );

    // Метод для получения папки по ID (вызовется GET /api/v1/folders/{folderId})
    @GET
    @Path("/{folderId}")
    @Produces(MediaType.APPLICATION_JSON)
    ListResponse<FileInfoResponse> getFolderContents(
            @PathParam("folderId") Long folderId,
            @QueryParam("page") int page,
            @QueryParam("size") int size
    );

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    FileInfoResponse createFolder(FolderCreateRequest request);

    @DELETE
    @Path("/{folderId}")
    @Produces(MediaType.APPLICATION_JSON)
    Response deleteFolder(@PathParam("folderId") Long folderId);
}