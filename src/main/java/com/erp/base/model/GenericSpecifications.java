package com.erp.base.model;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class GenericSpecifications<E> {
    private final List<CriteriaObject<?>> criteriaList = new ArrayList<>();
    public static final String EQ = "=";
    public static final String NEQ = "!=";
    public static final String GT = ">";
    public static final String GOE = ">=";
    public static final String LT = "<";
    public static final String LOE = "<=";
    public static final String LIKE = "%";
    public static final String IN = "in";

    public <T extends Comparable<T>> GenericSpecifications<E> add(String key, String operation, T value) {
        criteriaList.add(new CriteriaObject<>(key, operation, value));
        return this;
    }

    public Specification<E> build() {
        return (root, query, criteriaBuilder) -> {
            Predicate[] predicates = criteriaList.stream()
                    .map(criteria -> comparison(criteriaBuilder, root, criteria))
                    .filter(Objects::nonNull)
                    .toArray(Predicate[]::new);
            return criteriaBuilder.and(predicates);
        };
    }

    @SuppressWarnings("unchecked")
    private <T extends Comparable<T>> Predicate comparison(CriteriaBuilder criteriaBuilder, Root<E> root, CriteriaObject<?> criteria) {
        //剔除如果比較值為null就不當條件
        if (criteria != null && criteria.getOperator() != null && criteria.getValue() != null) {
            String key = criteria.getKey();
            String operator = criteria.getOperator();
            T value = (T) criteria.getValue();
            Path<T> properties = root.get(key);
            Predicate namePredicate;
            switch (operator) {
                case EQ -> namePredicate = criteriaBuilder.equal(properties, value);
                case NEQ -> namePredicate = criteriaBuilder.notEqual(properties, value);
                case GT -> namePredicate = criteriaBuilder.greaterThan(properties, value);
                case GOE -> namePredicate = criteriaBuilder.greaterThanOrEqualTo(properties, value);
                case LT -> namePredicate = criteriaBuilder.lessThan(properties, value);
                case LOE -> namePredicate = criteriaBuilder.lessThanOrEqualTo(properties, value);
                case LIKE -> namePredicate = criteriaBuilder.like((Path<String>) properties, "%" + value + "%");
                case IN -> namePredicate = root.get(key).in((Collection<? extends T>) value);
                // 可以根據需要添加其他操作符
                default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
            }
            return namePredicate;
        }
        return null;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CriteriaObject<T extends Comparable<T>> {
        private String key;
        private String operator;
        private T value;
    }
}

