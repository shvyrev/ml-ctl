package io.cx.ml.cli.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserAllContentResponse {
    private List<FolderInfo> folders;
    private List<FileInfo> files;
}
