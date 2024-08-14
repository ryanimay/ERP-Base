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
    public static final String BETWEEN = "between";

    public <T extends Comparable<T>> GenericSpecifications<E> add(String key, String operation, T value) {
        criteriaList.add(new CriteriaObject<>(key, operation, value));
        return this;
    }

    public <T extends Comparable<T>> GenericSpecifications<E> addBetween(String key, String operation, T[] value) {
        criteriaList.add(new CriteriaObject<>(key, operation, value));
        return this;
    }

    public Specification<E> buildAnd() {
        return (root, query, criteriaBuilder) -> {
            Predicate[] predicates = criteriaList.stream()
                    .map(criteria -> comparison(criteriaBuilder, root, criteria))
                    .filter(Objects::nonNull)
                    .toArray(Predicate[]::new);
            return criteriaBuilder.and(predicates);
        };
    }

    public Specification<E> buildOr() {
        return (root, query, criteriaBuilder) -> {
            Predicate[] predicates = criteriaList.stream()
                    .map(criteria -> comparison(criteriaBuilder, root, criteria))
                    .filter(Objects::nonNull)
                    .toArray(Predicate[]::new);
            return criteriaBuilder.or(predicates);
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
                case BETWEEN -> {
                    T[] values = (T[]) criteria.getValues();
                    if (values.length != 2) {
                        throw new IllegalArgumentException("BETWEEN operator requires exactly 2 values.");
                    }
                    namePredicate = criteriaBuilder.between(properties, values[0], values[1]);
                }
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
        private T[] values;

        public CriteriaObject(String key, String operator, T value) {
            this.key = key;
            this.operator = operator;
            this.value = value;
        }

        public CriteriaObject(String key, String operator, T[] values) {
            this.key = key;
            this.operator = operator;
            this.values = values;
        }
    }
}

