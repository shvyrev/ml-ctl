package io.cx.ml.cli.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode
@ToString
@JsonInclude(NON_NULL)
public class FileInfoResponse {
    private String id;
    private long size;
    private Long folderId;
    private String originalName, etag, path;
    private Boolean isFolder = false;
    @JsonIgnore
    private String minioBucket;
    @JsonIgnore
    private String storagePath;
    private FileStatus status;
    private LocalDateTime createdAt, uploadedAt;
}
