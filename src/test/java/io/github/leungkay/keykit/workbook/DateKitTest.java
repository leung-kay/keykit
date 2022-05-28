package io.github.leungkay.keykit.workbook;

import io.github.leungkay.keykit.date.DateKit;
import io.github.leungkay.keykit.date.DatePattern;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

@Slf4j
public class DateKitTest {
    @Test
    public void test2DateTime() {
        LocalDateTime now = DateKit.getNow();
        String dateStr = DateKit.from(now).toString(DatePattern.PRECISE_DATETIME);
        Long dateMilli = DateKit.from(now).toMilli();

        LocalDateTime dateTime = DateKit.from(dateStr, DatePattern.PRECISE_DATETIME).toTime();
        Assertions.assertEquals(now, dateTime);
        log.info(dateTime.toString());
        dateTime = DateKit.from(dateMilli).toTime();
        Assertions.assertEquals(now, dateTime);
        log.info(dateTime.toString());
    }

    @Test
    public void test2Milli() {
        String dateStr = "2022-11-11 11:11:11";
        LocalDateTime dateTime = DateKit.from(dateStr, DatePattern.STANDARD_DATETIME).toTime();

        Long dateMilli = DateKit.from(dateStr, DatePattern.STANDARD_DATETIME).toMilli();
        log.info(dateMilli.toString());
        dateMilli = DateKit.from(dateTime).toMilli();
        log.info(dateMilli.toString());
    }

    @Test
    public void test2Str() {
        String dateStr = "2022-11-11 11:11:11";
        LocalDateTime dateTime = DateKit.from(dateStr, DatePattern.STANDARD_DATETIME).toTime();

        Long dateMilli = DateKit.from(dateStr, DatePattern.STANDARD_DATETIME).toMilli();
        log.info(dateMilli.toString());
        dateMilli = DateKit.from(dateTime).toMilli();
        log.info(dateMilli.toString());
    }
}
