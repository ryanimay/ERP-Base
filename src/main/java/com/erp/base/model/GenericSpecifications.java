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

public class GenericSpecifications<E> {
    private final List<CriteriaObject<?>> criteriaList = new ArrayList<>();

    public <T extends Comparable<T>> GenericSpecifications<E> add(String key, String operation, T value) {
        criteriaList.add(new CriteriaObject<>(key, operation, value));
        return this;
    }

    public Specification<E> build() {
        return (root, query, criteriaBuilder) -> {
                Predicate predicate = criteriaBuilder.conjunction();
                for (CriteriaObject<?> criteria : criteriaList) {
                    comparison(predicate, criteriaBuilder, root, criteria);
                }
            return predicate;
        };
    }
    @SuppressWarnings("unchecked")
    private <T extends Comparable<T>> void comparison(Predicate predicate, CriteriaBuilder criteriaBuilder, Root<E> root, CriteriaObject<?> criteria) {
        if (criteria != null && criteria.getOperator() != null && criteria.getValue() != null) {
            String key = criteria.getKey();
            String operator = criteria.getOperator();
            T value = (T) criteria.getValue();
            Path<T> properties = root.get(key);
            switch (operator) {
                case "=" -> predicate.getExpressions().add(criteriaBuilder.equal(properties, value));
                case "!=" -> predicate.getExpressions().add(criteriaBuilder.notEqual(properties, value));
                case ">" -> predicate.getExpressions().add(criteriaBuilder.greaterThan(properties, value));
                case ">=" -> predicate.getExpressions().add(criteriaBuilder.greaterThanOrEqualTo(properties, value));
                case "<" -> predicate.getExpressions().add(criteriaBuilder.lessThan(properties, value));
                case "<=" -> predicate.getExpressions().add(criteriaBuilder.lessThanOrEqualTo(properties, value));
                case "like" -> predicate.getExpressions().add(criteriaBuilder.like((Path<String>) properties, "%" + value + "%"));
                case "in" -> predicate.getExpressions().add(root.get(key).in((Collection<? extends T>)value));
                // 可以根據需要添加其他操作符
                default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
            }
        }
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
