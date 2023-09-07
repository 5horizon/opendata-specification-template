package ru.isands.lib.specification.template.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.isands.lib.specification.template.configuration.properties.FilterProperties;
import ru.isands.lib.specification.template.configuration.properties.SortProperties;
import ru.isands.lib.specification.template.service.PageableService;
import ru.isands.lib.specification.template.util.SortUtil;
import ru.isands.lib.specification.template.util.SpecificationUtil;
import ru.isands.lib.specification.template.view.OrderCriteria;
import ru.isands.lib.specification.template.view.SearchCriteria;

import java.util.Collection;
import java.util.UUID;
import java.util.regex.Pattern;

public abstract class AbstractPageableService<T, R extends JpaRepository<T, UUID> & JpaSpecificationExecutor<T>> implements PageableService<T, R> {
    private final R jpaSpecificationExecutor;
    private final FilterProperties filterProperties;
    private final SortProperties sortProperties;

    private final Pattern filterPattern;
    private final Pattern sortPattern;

    protected AbstractPageableService(R jpaSpecificationExecutor,
                                      FilterProperties filterProperties,
                                      SortProperties sortProperties) {
        this.jpaSpecificationExecutor = jpaSpecificationExecutor;
        this.filterProperties = filterProperties;
        this.sortProperties = sortProperties;
        this.filterPattern = Pattern.compile(filterProperties.getPattern());
        this.sortPattern = Pattern.compile(sortProperties.getPattern());
    }

    public Page<T> getPage(
            int number,
            int size,
            String search,
            String sort) {
        if (search.isEmpty() && sort.isEmpty()) {
            return jpaSpecificationExecutor.findAll(PageRequest.of(number, size));
        }
        Specification<T> spec = null;
        Sort fullSort = null;
        Pageable pageable;

        if (!search.isEmpty()) {
            spec = SpecificationUtil.parseSpecification(search,
                                                        filterProperties);
        }

        if (!sort.isEmpty()) {
            fullSort = SortUtil.parseSort(sort, sortPattern, sortProperties.getFieldSeparator());
        }
        pageable = fullSort == null
                ? PageRequest.of(number, size)
                : PageRequest.of(number, size, fullSort);
        return jpaSpecificationExecutor.findAll(spec, pageable);
    }

    public Page<T> getPage(
            int number,
            int size,
            Collection<SearchCriteria> search,
            Collection<OrderCriteria> sort) {
        if (search.isEmpty() && sort.isEmpty()) {
            return jpaSpecificationExecutor.findAll(PageRequest.of(number, size));
        }
        Specification<T> spec = null;
        Sort fullSort = null;
        Pageable pageable;

        if (!search.isEmpty()) {
            spec = SpecificationUtil.parseSpecification(search);
        }

        if (!sort.isEmpty()) {
            fullSort = SortUtil.parseSort(sort);
        }
        pageable = fullSort == null
                ? PageRequest.of(number, size)
                : PageRequest.of(number, size, fullSort);
        return jpaSpecificationExecutor.findAll(spec, pageable);
    }

    public Page<T> getPage(
            int number,
            int size,
            Collection<SearchCriteria> search,
            String sort) {
        if (search.isEmpty() && sort.isEmpty()) {
            return jpaSpecificationExecutor.findAll(PageRequest.of(number, size));
        }
        Specification<T> spec = null;
        Sort fullSort = null;
        Pageable pageable;

        if (!search.isEmpty()) {
            spec = SpecificationUtil.parseSpecification(search);
        }

        if (!sort.isEmpty()) {
            fullSort = SortUtil.parseSort(sort, sortPattern, sortProperties.getFieldSeparator());
        }
        pageable = fullSort == null
                ? PageRequest.of(number, size)
                : PageRequest.of(number, size, fullSort);
        return jpaSpecificationExecutor.findAll(spec, pageable);
    }

    public Page<T> getPage(
            int number,
            int size,
            String search,
            Collection<OrderCriteria> sort) {
        if (search.isEmpty() && sort.isEmpty()) {
            return jpaSpecificationExecutor.findAll(PageRequest.of(number, size));
        }
        Specification<T> spec = null;
        Sort fullSort = null;
        Pageable pageable;

        if (!search.isEmpty()) {
            spec = SpecificationUtil.parseSpecification(search,
                                                        filterProperties);
        }

        if (!sort.isEmpty()) {
            fullSort = SortUtil.parseSort(sort);
        }
        pageable = fullSort == null
                ? PageRequest.of(number, size)
                : PageRequest.of(number, size, fullSort);
        return jpaSpecificationExecutor.findAll(spec, pageable);
    }


}
