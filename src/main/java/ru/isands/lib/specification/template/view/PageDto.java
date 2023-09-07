package ru.isands.lib.specification.template.view;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PageDto<T>{

    private List<T> content;
    private boolean last;
    private int totalPages;
    private long totalElements;
    private boolean first;
    private int number;
    private int numberOfElements;
    private int size;
    private boolean empty;
}
