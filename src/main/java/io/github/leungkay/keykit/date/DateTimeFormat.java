package io.github.leungkay.keykit.date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
@AllArgsConstructor
public class DateTimeFormat implements ITimeFormat {
    private LocalDateTime dateTime;

    @NonNull
    @Override
    public LocalDateTime toTime() {
        return this.dateTime;
    }

    @NonNull
    @Override
    public LocalDate toDate() {
        return dateTime.toLocalDate();
    }

    @NonNull
    @Override
    public Long toMilli() {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
