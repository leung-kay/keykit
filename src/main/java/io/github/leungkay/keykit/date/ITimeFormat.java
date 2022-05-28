package io.github.leungkay.keykit.date;

import lombok.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ITimeFormat {
    default String toString(DatePattern pattern) {
        switch (pattern) {
            case PRECISE_DATETIME:
                return DatePattern.PRECISE_DATETIME.pattern().format(toTime());
            case STANDARD_DATETIME:
                return DatePattern.STANDARD_DATETIME.pattern().format(toTime());
            case COMPACT_DATETIME:
                return DatePattern.COMPACT_DATETIME.pattern().format(toTime());
            case STANDARD_DATE:
                return DatePattern.STANDARD_DATE.pattern().format(toDate());
            case COMPACT_DATE:
                return DatePattern.COMPACT_DATE.pattern().format(toDate());
            default:
                return null;

        }
    }

    @NonNull LocalDateTime toTime();

    @NonNull LocalDate toDate();

    @NonNull Long toMilli();
}
