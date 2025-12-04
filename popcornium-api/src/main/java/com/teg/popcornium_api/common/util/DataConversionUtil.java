package com.teg.popcornium_api.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class DataConversionUtil {

    public Optional<Double> parseRating(String ratingString) {
        if (ratingString == null || ratingString.isBlank()) {
            return Optional.empty();
        }
        try {
            String normalizedRating = ratingString.replace(',', '.');
            return Optional.of(Double.parseDouble(normalizedRating));
        } catch (NumberFormatException e) {
            log.error("Rating parsing failed for value: {}", ratingString);
            return Optional.empty();
        }
    }

    public Optional<Integer> parseProductionYear(String yearString) {
        if (yearString == null || yearString.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(Integer.parseInt(yearString));
        } catch (NumberFormatException e) {
            log.error("Year parsing failed for value: {}", yearString);
            return Optional.empty();
        }
    }
}
