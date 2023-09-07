package ru.isands.lib.specification.template.util;

import org.springframework.data.domain.Sort;
import ru.isands.lib.specification.template.view.OrderCriteria;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SortUtil {

    public static Sort parseSort(
            String sort,
            String patternRegex,
            String fieldSeparator) {
        return parseSort(sort,
                         Pattern.compile(patternRegex),
                         fieldSeparator);
    }

    public static Sort parseSort(
            String sort,
            Pattern pattern,
            String fieldSeparator) {
        if (fieldSeparator.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Sort fullSort = null;
        Matcher matcher = pattern.matcher(sort + fieldSeparator);
        while (matcher.find()) {
            Sort newCriteria = Sort.by(Sort.Direction.fromString(matcher.group(3)), matcher.group(1));
            fullSort = fullSort == null
                    ? newCriteria
                    : fullSort.and(newCriteria);
        }
        return fullSort;
    }

    public static Sort parseSort(
            Collection<OrderCriteria> criterias) {
        Sort fullSort = null;
        for (OrderCriteria criteria : criterias) {
            Sort newCriteria = Sort.by(criteria.getDirection(), criteria.getKey());
            fullSort = fullSort == null
                    ? newCriteria
                    : fullSort.and(newCriteria);
        }
        return fullSort;
    }
}
