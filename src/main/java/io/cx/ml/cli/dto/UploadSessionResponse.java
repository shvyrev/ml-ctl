package io.cx.ml.cli.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode
@ToString
@JsonInclude(NON_NULL)
public class UploadSessionResponse {
    private UUID sessionId, fileId;
    private ResponseType type;
    private String etag;
    private String path;
    private Integer chunkSize;
    private long uploadOffset;
}