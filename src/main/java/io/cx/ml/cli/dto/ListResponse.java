package io.cx.ml.cli.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode
@ToString
public class ListResponse<V> {
    private List<V> data;
    private Paging paging;

    public static <V>ListResponse<V> of(Long count, Integer pageCount, List<V> items, int currentPage) {
        return new ListResponse<V>()
                .setPaging(new Paging(count, pageCount, currentPage))
                .setData(items);
    }
}
