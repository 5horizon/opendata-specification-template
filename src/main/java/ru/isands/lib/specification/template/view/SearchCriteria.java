package ru.isands.lib.specification.template.view;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class SearchCriteria {
    private String key;
    private String operation;
    private String value;

    public SearchCriteria(String key, String operation, String value) {
        this.key = key;
        this.operation = operation;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SearchCriteria criteria = (SearchCriteria) o;
        return key.equals(criteria.key) && operation.equals(criteria.operation) && Objects.equals(value,
                                                                                                  criteria.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, operation, value);
    }

    @Override
    public String toString() {
        return key + operation + value;
    }
}
