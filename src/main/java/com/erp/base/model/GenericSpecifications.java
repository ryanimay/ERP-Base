package com.erp.base.model;

import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class GenericSpecifications<T> {
    private final List<CriteriaObject> criteriaList = new ArrayList<>();

    public GenericSpecifications<T> add(String key, String operation, String value) {
        criteriaList.add(new CriteriaObject(key, operation, value));
        return this;
    }

    public Specification<T> build() {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (CriteriaObject criteria : criteriaList) {
                if (criteria.getOperation().equalsIgnoreCase(">")) {
                    predicates.add(criteriaBuilder.greaterThan(root.get(criteria.getKey()), criteria.getValue()));
                } else if (criteria.getOperation().equalsIgnoreCase("<")) {
                    predicates.add(criteriaBuilder.lessThan(root.get(criteria.getKey()), criteria.getValue()));
                } else if (criteria.getOperation().equalsIgnoreCase("=")) {
                    if (root.get(criteria.getKey()).getJavaType() == String.class) {
                        predicates.add(criteriaBuilder.like(root.get(criteria.getKey()), "%" + criteria.getValue() + "%"));
                    } else {
                        predicates.add(criteriaBuilder.equal(root.get(criteria.getKey()), criteria.getValue()));
                    }
                } else if (criteria.getOperation().equalsIgnoreCase(">=")) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(criteria.getKey()), criteria.getValue()));
                } else if (criteria.getOperation().equalsIgnoreCase("<=")) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(criteria.getKey()), criteria.getValue()));
                }
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CriteriaObject {
        private String key;
        private String operation;
        private String value;
    }
}
