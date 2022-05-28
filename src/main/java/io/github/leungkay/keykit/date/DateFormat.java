package io.github.leungkay.keykit.date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
@AllArgsConstructor
public class DateFormat implements ITimeFormat {
    private LocalDate date;

    @NonNull
    @Override
    public LocalDateTime toTime() {
        return date.atStartOfDay();
    }

    @NonNull
    @Override
    public LocalDate toDate() {
        return date;
    }

    @NonNull
    @Override
    public Long toMilli() {
        return date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
