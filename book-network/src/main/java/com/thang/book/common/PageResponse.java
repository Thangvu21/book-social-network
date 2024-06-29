package com.thang.book.common;

import com.thang.book.book.BookResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResponse<T> {

    private int number;

    private int size;

    private List<T> content;

    private long totalElements;

    private int totalPages;

    private boolean first;

    private boolean last;
}
