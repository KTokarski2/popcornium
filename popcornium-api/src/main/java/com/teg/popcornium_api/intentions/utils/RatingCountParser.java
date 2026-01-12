package com.teg.popcornium_api.intentions.utils;

public class RatingCountParser {
    private static final char NBSP = '\u00A0';

    private RatingCountParser() {}

    public static Integer parse(String raw) {
        if (raw == null) return null;

        try {
            String cleaned = raw
                    .replace(NBSP, ' ')
                    .replace("tys.", "")
                    .replace("ocen", "")
                    .trim();

            return Integer.parseInt(cleaned) * 1_000;
        } catch (Exception e) {
            return null;
        }
    }
}
