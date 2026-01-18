package com.teg.popcornium_api.common.util;

import com.teg.popcornium_api.common.model.Movie;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class MovieSpecification {

    public static Specification<Movie> filter(
            String query,
            Integer yearFrom,
            Integer yearTo,
            Double ratingFrom,
            Double ratingTo
    ) {
        return (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (query != null && !query.isBlank()) {
                String like = "%" + query.toLowerCase() + "%";

                predicates.add(
                        cb.or(
                                cb.like(cb.lower(root.get("polishTitle")), like),
                                cb.like(cb.lower(root.get("originalTitle")), like),
                                cb.equal(root.get("productionYear"), parseInt(query))
                        )
                );
            }

            if (yearFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("productionYear"), yearFrom));
            }

            if (yearTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("productionYear"), yearTo));
            }

            if (ratingFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("rating"), ratingFrom));
            }

            if (ratingTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("rating"), ratingTo));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Integer parseInt(String value) {
        try {
            return Integer.valueOf(value);
        } catch (Exception e) {
            return null;
        }
    }
}
