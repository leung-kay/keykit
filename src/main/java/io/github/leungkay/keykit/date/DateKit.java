package io.github.leungkay.keykit.date;

import lombok.NonNull;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DateKit {

    private DateKit() {
    }

    public static final long SECONDS_IN_1MINUTE = 60L;
    public static final long SECONDS_IN_1HOUR = SECONDS_IN_1MINUTE * 60;
    public static final long SECONDS_IN_1DAY = SECONDS_IN_1HOUR * 24;
    public static final long SECONDS_IN_30DAYS = SECONDS_IN_1DAY * 30;

    public static long getCurrentMilli() {
        return Instant.now().toEpochMilli();
    }

    public static Long parse2Milli(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static Long parse2Milli(LocalDate date) {
        if (date == null) return null;
        return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static LocalDate getToday() {
        return LocalDate.now();
    }

    public static LocalDate getYesterday() {
        return getToday().minusDays(1L);
    }

    public static LocalDate getPastDay(LocalDate date, long days) {
        return date.minusDays(days);
    }

    public static LocalDateTime getPastDay(LocalDateTime date, long days) {
        return date.minusDays(days);
    }

    public static List<LocalDate> getPastDays(LocalDate begin, int size) {
        List<LocalDate> days = new ArrayList<>();
        // 最后一位放当前日期
        days.add(begin);
        for (long i = 1; i < size; i++) {
            days.add(0, getPastDay(begin, i));
        }
        return days;
    }

    public static List<LocalDateTime> getPastDays(LocalDateTime begin, LocalDateTime end, int size) {
        List<LocalDateTime> days = new ArrayList<>();
        // 第一位放开始时间，最后一位放当前时间
        days.add(begin);
        LocalDateTime endZeroTime = LocalDateTime.of(end.getYear(), end.getMonth(), end.getDayOfMonth(), 0, 0);
        for (long i = (long) size - 1; i >= 0; i--) {
            days.add(getPastDay(endZeroTime, i));
        }
        days.add(end);
        return days;
    }

    public static List<LocalDate> getPastDays(LocalDate begin, LocalDate end) {
        long days = Duration.between(begin.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant(), end.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()).toDays();
        return getPastDays(end, (int) days + 1);
    }

    public static List<LocalDateTime> getPastDays(LocalDateTime begin, LocalDateTime end) {
        long days = Duration.between(begin.atZone(ZoneId.systemDefault()).toInstant(), end.atZone(ZoneId.systemDefault()).toInstant()).toDays();
        return getPastDays(begin, end, (int) days);
    }

    public static LocalDateTime getNow() {
        return LocalDateTime.now();
    }

    public static LocalDateTime getTodayTime() {
        return LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
    }

    public static LocalDateTime getPastTime(LocalDateTime dateTime, Long seconds) {
        if (dateTime == null || seconds == null) return null;
        return dateTime.minusSeconds(seconds);
    }

    public static LocalDateTime parse2DateTime(Long milli) {
        if (milli == null) return null;
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(milli), ZoneId.systemDefault());
    }

    public static LocalDate parse2Date(Long millis) {
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDateTime parse2LocalDateTime(String time) {
        time = time.substring(0, 19);
        return LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static @NonNull ITimeFormat from(String string, DatePattern pattern) {
        switch (pattern) {
            case PRECISE_DATETIME:
                return new DateTimeFormat(LocalDateTime.parse(string, DatePattern.PRECISE_DATETIME.pattern()));
            case STANDARD_DATETIME:
                return new DateTimeFormat(LocalDateTime.parse(string, DatePattern.STANDARD_DATETIME.pattern()));
            case COMPACT_DATETIME:
                return new DateTimeFormat(LocalDateTime.parse(string, DatePattern.COMPACT_DATETIME.pattern()));
            case STANDARD_DATE:
                return new DateFormat(LocalDate.parse(string, DatePattern.STANDARD_DATE.pattern()));
            case COMPACT_DATE:
                return new DateFormat(LocalDate.parse(string, DatePattern.COMPACT_DATE.pattern()));
            default:
                throw new RuntimeException();
        }
    }

    public static @NonNull ITimeFormat from(LocalDateTime time) {
        return new DateTimeFormat(time);
    }

    public static @NonNull ITimeFormat from(LocalDate date) {
        return new DateFormat(date);
    }

    public static @NonNull ITimeFormat from(Long milli) {
        return new MilliFormat(milli);
    }


}
