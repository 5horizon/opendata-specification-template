package ru.isands.lib.specification.template.service.builder;

import org.springframework.data.jpa.domain.Specification;
import ru.isands.lib.specification.template.configuration.properties.FilterProperties;
import ru.isands.lib.specification.template.service.SpecificationBuilder;
import ru.isands.lib.specification.template.service.impl.AbstractSpecification;
import ru.isands.lib.specification.template.view.SearchCriteria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class AbstractSpecificationBuilder<T> implements SpecificationBuilder<T> {
    private final Collection<SearchCriteria> filter;
    private FilterProperties properties;

    public AbstractSpecificationBuilder(FilterProperties properties) {
        this.properties = properties;
        filter = new LinkedList<>();
    }

    public AbstractSpecificationBuilder(Collection<SearchCriteria> criterias) {
        filter = criterias;
    }

    @Override
    public SpecificationBuilder<T> with(String key, String operation, String value) {
        filter.add(new SearchCriteria(key, operation, value));
        return this;
    }

    @Override
    public SpecificationBuilder<T> with(SearchCriteria criteria) {
        filter.add(criteria);
        return this;
    }


    @Override
    public Specification<T> build() {
        if (filter.size() == 0) {
            return null;
        }

        List<Specification<T>> specs = filter.stream()
                .map(c -> new AbstractSpecification<T>(c, properties) {
                })
                .collect(Collectors.toList());
        Specification<T> result = createNameOrTagResult(specs);
        if (result.equals(specs.get(0))) {
            result = addSpecifications(result, specs, 1);
        } else {
            List<Specification<T>> addingSpecs = new ArrayList<>();
            for (Specification<T> spec : specs) {
                AbstractSpecification<T> abstractSpec = (AbstractSpecification<T>) spec;
                if (!abstractSpec.getCriteria().getKey().equals("name") && !abstractSpec.getCriteria().getKey().equals("tags.id")) {
                    addingSpecs.add(spec);
                }
            }
            result = addSpecifications(result, addingSpecs, 0);
        }
        return result;
    }

    private Specification<T> addSpecifications(Specification<T> firstSpec, List<Specification<T>> specs,
                                               int numberStart) {
        Specification<T> result = firstSpec;
        for (int i = numberStart; i < specs.size(); i++) {
            result = Specification.where(result)
                    .and(specs.get(i));
        }
        return result;
    }

    private Specification<T> createNameOrTagResult(List<Specification<T>> specs) {
        Specification<T> result = specs.get(0);
        Specification<T> nameSpec = null;
        Specification<T> tagSpec = null;
        for (Specification<T> spec : specs) {
            AbstractSpecification<T> abstractSpec = (AbstractSpecification<T>) spec;
            if (abstractSpec.getCriteria().getKey().equals("name")) {
                nameSpec = spec;
            }
            if (abstractSpec.getCriteria().getKey().equals("tags.id")) {
                tagSpec = spec;
            }
        }
        if (nameSpec != null && tagSpec != null) {
            result = Specification.where(nameSpec.or(tagSpec));
        }
        return result;
    }
}
