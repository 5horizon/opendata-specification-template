package ru.isands.lib.specification.template.util;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.isands.lib.specification.template.configuration.properties.FilterProperties;
import ru.isands.lib.specification.template.configuration.properties.SortProperties;
import ru.isands.lib.specification.template.view.OrderCriteria;
import ru.isands.lib.specification.template.view.SearchCriteria;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

@Component
public class CriteriaUtil {

    public List<SearchCriteria> parseSearchCriteria(
            String search,
            FilterProperties properties) {

        List<SearchCriteria> result = new LinkedList<>();
        if (properties.getFieldSeparator() == null || properties.getFieldSeparator().isEmpty()) {
            throw new IllegalArgumentException();
        }
        Matcher matcher = properties.getFilterPattern().matcher(search + properties.getFieldSeparator());
        while (matcher.find()) {
            result.add(new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3)));
        }

        return result;
    }

    public List<OrderCriteria> parseOrderCriteria(
            String sort,
            SortProperties properties) {

        List<OrderCriteria> result = new LinkedList<>();
        if (properties.getFieldSeparator() == null || properties.getFieldSeparator().isEmpty()) {
            throw new IllegalArgumentException();
        }
        Matcher matcher = properties.getSortPattern().matcher(sort + properties.getFieldSeparator());
        while (matcher.find()) {
            result.add(new OrderCriteria(matcher.group(1), Sort.Direction.fromString(matcher.group(3))));
        }

        return result;
    }

    public Sort parseSort(
            String sort,
            SortProperties properties) {

        if (properties.getFieldSeparator().isEmpty()) {
            throw new IllegalArgumentException();
        }
        Sort fullSort = null;
        Matcher matcher = properties.getSortPattern().matcher(sort + properties.getFieldSeparator());
        while (matcher.find()) {
            Sort newCriteria = Sort.by(Sort.Direction.fromString(matcher.group(3)), matcher.group(1));
            fullSort = fullSort == null
                    ? newCriteria
                    : fullSort.and(newCriteria);
        }
        return fullSort;
    }
}
