package io.cx.ml.cli.dto;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;

import java.io.File;

public class FileMultipartForm {

    @RestForm
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public File file;

    @RestForm
    @PartType(MediaType.TEXT_PLAIN)
    public String fileName;

    @RestForm
    @PartType(MediaType.TEXT_PLAIN)
    public Long folderId;
}