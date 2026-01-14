package io.cx.ml.cli.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode
@ToString
@JsonInclude(NON_NULL)
public class UploadSessionRequest {
    @JsonProperty("size")
    private long uploadLength;

    @JsonProperty("fileName")
    private String fileName;

    @JsonProperty("etag")
    private String expectedEtag;

    /**
     * ID родительской папки, куда будет загружен файл.
     */
    @JsonProperty("folderId")
    private Long folderId;
}