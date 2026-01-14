package io.cx.ml.cli.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class FolderCreateRequest {

    /**
     * Имя новой папки (без слешей).
     */
    @JsonProperty("name")
    private String name;

    /**
     * ID родительской папки, в которой будет создана новая папка.
     */
    @JsonProperty("parentId")
    private Long parentId;
}