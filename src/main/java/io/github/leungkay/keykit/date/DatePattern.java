package io.github.leungkay.keykit.date;

import java.time.format.DateTimeFormatter;

public enum DatePattern {
    PRECISE_DATETIME("yyyy-MM-dd HH:mm:ss.n"),
    STANDARD_DATETIME("yyyy-MM-dd HH:mm:ss"),
    COMPACT_DATETIME("yyyyMMddHHmmss"),
    STANDARD_DATE("yyyy-MM-dd"),
    COMPACT_DATE("yyyyMMdd");

    private final String value;

    @Override
    public String toString() {
        return value;
    }

    DatePattern(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public DateTimeFormatter pattern() {
        return DateTimeFormatter.ofPattern(value());
    }
}
