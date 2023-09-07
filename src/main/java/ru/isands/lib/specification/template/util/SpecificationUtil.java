package ru.isands.lib.specification.template.util;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.isands.lib.specification.template.configuration.properties.FilterProperties;
import ru.isands.lib.specification.template.service.SpecificationBuilder;
import ru.isands.lib.specification.template.service.builder.AbstractSpecificationBuilder;
import ru.isands.lib.specification.template.view.SearchCriteria;

import java.util.Collection;
import java.util.regex.Matcher;

@Component
public class SpecificationUtil {
    private final CriteriaUtil criteriaUtil;

    public SpecificationUtil(CriteriaUtil criteriaUtil) {
        this.criteriaUtil = criteriaUtil;
    }

    public static <T> Specification<T> parseSpecification(
            String search,
            FilterProperties properties) {
        return parseSpecification(search, properties, new AbstractSpecificationBuilder<T>(properties));
    }

    public static <T> Specification<T> parseSpecification(
            Collection<SearchCriteria> criterias) {
        return new AbstractSpecificationBuilder<T>(criterias).build();
    }

    public static <T> Specification<T> parseSpecification(
            String search,
            FilterProperties properties,
            SpecificationBuilder<T> builder) {
        if (properties.getFieldSeparator() == null || properties.getFieldSeparator().isEmpty()) {
            throw new IllegalArgumentException();
        }
        Matcher matcher = properties.getFilterPattern().matcher(search + properties.getFieldSeparator());
        while (matcher.find()) {
            builder.with(new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3)));
        }
        return builder.build();
    }

    public static String serializeCriteria(String key, String operation, Collection<String> value) {
        return serializeCriteria(key, operation, String.join("|", value));
    }

    public static String serializeCriteria(String key, String operation, String value) {
        return key + operation + value;
    }
}
