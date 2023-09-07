package ru.isands.lib.specification.template.service;

import org.springframework.data.jpa.domain.Specification;
import ru.isands.lib.specification.template.view.SearchCriteria;

public interface SpecificationBuilder<T> {
    SpecificationBuilder<T> with(String key, String operation, String value);

    SpecificationBuilder<T> with(SearchCriteria criteria);

    Specification<T> build();
}
