package ru.isands.lib.specification.template.service.impl;

import lombok.Getter;
import org.springframework.data.jpa.domain.Specification;
import ru.isands.lib.specification.template.configuration.properties.FilterProperties;
import ru.isands.lib.specification.template.view.SearchCriteria;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public abstract class AbstractSpecification<T> implements Specification<T> {

    private final SearchCriteria criteria;
    private final FilterProperties filterProperties;

    public AbstractSpecification(SearchCriteria searchCriteria,
                                 FilterProperties properties) {
        criteria = searchCriteria;
        filterProperties = properties;
    }

    @Override
    public Predicate toPredicate
            (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Path path = null;
        query.distinct(true);
        String[] levels = filterProperties.getLevelSeparator() == null || filterProperties.getLevelSeparator().isEmpty()
                ? new String[]{criteria.getKey()}
                : criteria.getKey().split(filterProperties.getLevelSeparator());
        if (levels.length > 1) {
            Join chain = null;
            for (int i = 0; i < levels.length - 1; i++) {
                if (chain == null) {
                    chain = root.join(levels[i], JoinType.LEFT);
                } else {
                    chain = chain.join(levels[i], JoinType.LEFT);
                }
            }
            path = chain.get(levels[levels.length - 1]);
        } else if (this.criteria.getOperation().equals("!!")) {
            path = root.join(criteria.getKey(), JoinType.LEFT);
        } else {
            path = root.get(criteria.getKey());
        }
        return getPredicate(builder, path, criteria.getOperation().toUpperCase(Locale.ROOT), criteria.getValue());
    }

    private Predicate getPredicate(CriteriaBuilder builder,
                                   Path path,
                                   String operation,
                                   String value) {
        Predicate result = null;
        switch (operation) {
            case "::": {
                result = getEqualPredicate(builder, path, value);
                break;
            }
            case ">>": {
                if (LocalDate.class.equals(path.getJavaType())) {
                    result = builder.greaterThan(path, (LocalDate) parseByFieldType(path.getJavaType(), value));
                    break;
                }
                result = builder.greaterThan(path, value);
                break;
            }
            case ">:": {
                if (LocalDate.class.equals(path.getJavaType())) {
                    result = builder.greaterThanOrEqualTo(path, (LocalDate) parseByFieldType(path.getJavaType(), value));
                    break;
                }
                result = builder.greaterThanOrEqualTo(path, value);
                break;
            }
            case "<<": {
                if (LocalDate.class.equals(path.getJavaType())) {
                    result = builder.lessThan(path, (LocalDate) parseByFieldType(path.getJavaType(), value));
                    break;
                }
                result = builder.lessThan(path, value);
                break;
            }
            case "<:": {
                if (LocalDate.class.equals(path.getJavaType())) {
                    result = builder.lessThanOrEqualTo(path, (LocalDate) parseByFieldType(path.getJavaType(), value));
                    break;
                }
                result = builder.lessThanOrEqualTo(path, value);
                break;
            }
            case "!:": {
                result = builder.not(getEqualPredicate(builder, path, value));
                break;
            }
            case "<>": {
                result = getTimeBetweenPredicate(builder, path, value);
                break;
            }
            case "!!": {
                List<UUID> values = Arrays.stream(value.split("\\|"))
                        .map(UUID::fromString)
                        .collect(Collectors.toList());
                result = builder.in(path).value(values);
                break;
            }
        }
        return result;
    }

    private Predicate getEqualPredicate(CriteriaBuilder builder, Path path, String value) {
        Predicate result;
        List<String> values = Arrays.asList(value.split("\\|"));
        Class javaType = path.getJavaType();
        if (values.size() == 1) {
            if ("null".equalsIgnoreCase(values.get(0))) {
                result = builder.isNull(path);
            } else {
                if (String.class == javaType) {
                    if (values.get(0).startsWith("%") || values.get(0).endsWith("%")) {
                        result = builder.like(builder.lower(path), values.get(0).toLowerCase());
                    } else {
                        result = builder.equal(builder.lower(path), values.get(0).toLowerCase());
                    }
                } else {
                    result = builder.equal(path, parseByFieldType(javaType, value));
                }
            }
        } else {
            if (String.class == javaType) {
                Path finalPath = path;
                result = builder.or((Predicate[]) values.stream()
                        .map(val -> builder.like(builder.lower(finalPath),
                                "%" + val.toLowerCase() + "%"))
                        .toArray(Predicate[]::new));
            } else {
                List<Object> parsedValues = values.stream()
                        .map(val -> parseByFieldType(javaType, val))
                        .collect(Collectors.toList());
                result = builder.in(path).value(parsedValues);
            }
        }
        return result;
    }

    private Object parseByFieldType(Class fieldType, String value) {
        Object result;
        if (Boolean.class.equals(fieldType)) {
            result = Boolean.parseBoolean(value);
        } else if (UUID.class.equals(fieldType)) {
            result = UUID.fromString(value);
        } else if (Long.class.equals(fieldType)) {
            result = Long.parseLong(value);
        } else if (Integer.class.equals(fieldType)) {
            result = Integer.parseInt(value);
        } else if (Float.class.equals(fieldType)) {
            result = Float.parseFloat(value);
        } else if (OffsetDateTime.class.equals(fieldType)) {
            result = filterProperties.getOffsetDateTimeFormatter() == null
                    ? OffsetDateTime.parse(value)
                    : OffsetDateTime.parse(value, filterProperties.getOffsetDateTimeFormatter());
        } else if (LocalDate.class.equals(fieldType)) {
            result = filterProperties.getLocalDateFormatter() == null
                    ? LocalDate.parse(value)
                    : LocalDate.parse(value, filterProperties.getLocalDateFormatter());
        } else {
            result = value;
        }
        return result;
    }

    private Predicate getTimeBetweenPredicate(CriteriaBuilder builder, Path path, String value) {
        List<String> values = Arrays.asList(value.split("\\|"));
        OffsetDateTime systemTime = OffsetDateTime.now();
        if (values.size() == 1) {
            LocalDate valueDay = LocalDate.parse(value);
            OffsetDateTime startDay = OffsetDateTime.of(valueDay, LocalTime.MIN, systemTime.getOffset());
            OffsetDateTime endDay = OffsetDateTime.of(valueDay, LocalTime.MAX, systemTime.getOffset());
            return builder.between(path, startDay, endDay);
        } else {
            values.sort(Comparator.comparing(LocalDate::parse));
            List<LocalDate> valueDays = values.stream()
                    .map(LocalDate::parse)
                    .collect(Collectors.toList());
            return builder.or((Predicate[]) valueDays.stream()
                    .map(date -> builder.between(path, OffsetDateTime.of(date, LocalTime.MIN, systemTime.getOffset()),
                            OffsetDateTime.of(date, LocalTime.MAX, systemTime.getOffset())))
                    .toArray(Predicate[]::new));
        }
    }

}
