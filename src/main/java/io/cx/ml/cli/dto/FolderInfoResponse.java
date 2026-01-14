package io.cx.ml.cli.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Getter
@Setter
@Accessors(chain = true)
public class FolderInfoResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    /**
     * ID родительской папки (может быть null для корневой папки).
     */
    @JsonProperty("parentId")
    private Long parentId;

    @JsonProperty("owner")
    private String userId;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("fullPath")
    private String fullPath;
}