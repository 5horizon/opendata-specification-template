package ru.isands.lib.specification.template.service;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.isands.lib.specification.template.view.OrderCriteria;
import ru.isands.lib.specification.template.view.SearchCriteria;

import java.util.Collection;
import java.util.UUID;

public interface PageableService<T, R extends JpaRepository<T, UUID> & JpaSpecificationExecutor<T>> {

    Page<T> getPage(int number, int size, String search, String sort);

    Page<T> getPage(int number, int size, Collection<SearchCriteria> search, Collection<OrderCriteria> sort);

    Page<T> getPage(int number, int size, String search, Collection<OrderCriteria> sort);

    Page<T> getPage(int number, int size, Collection<SearchCriteria> search, String sort);
}
