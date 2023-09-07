package ru.isands.lib.specification.template.view;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;

@Getter
@Setter
@NoArgsConstructor
public class OrderCriteria {
    private String key;
    private Sort.Direction direction;

    public OrderCriteria(String key, Sort.Direction direction) {
        this.key = key;
        this.direction = direction;
    }
}
