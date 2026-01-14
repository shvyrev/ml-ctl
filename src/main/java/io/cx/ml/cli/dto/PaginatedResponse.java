package io.cx.ml.cli.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;

@RegisterForReflection
public class PaginatedResponse<T> {
    @JsonProperty("data")
    private List<T> data;
    @JsonProperty("paging")
    private Paging paging;

    // геттеры и сеттеры
    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }
}