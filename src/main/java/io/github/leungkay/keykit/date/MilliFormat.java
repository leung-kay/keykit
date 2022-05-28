package io.github.leungkay.keykit.date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
@AllArgsConstructor
public class MilliFormat implements ITimeFormat {
    private Long milli;

    @NonNull
    @Override
    public LocalDateTime toTime() {
        return Instant.ofEpochMilli(milli).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    @NonNull
    @Override
    public LocalDate toDate() {
        return Instant.ofEpochMilli(milli).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    @NonNull
    @Override
    public Long toMilli() {
        return milli;
    }
}
