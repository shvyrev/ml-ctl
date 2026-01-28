package io.cx.ml.cli.dto;


import lombok.Data;

@Data
public class FileInfo {
    private String id, name, path;
    private long size;
}