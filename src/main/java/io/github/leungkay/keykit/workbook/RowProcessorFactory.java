package io.github.leungkay.keykit.workbook;

import lombok.NonNull;

import java.util.List;
import java.util.Map;

public class RowProcessorFactory {
    @SuppressWarnings("unchecked")
    public static <T> RowProcessor<T> getRowProcessor(@NonNull Class<T> clazz, String... fields) {
        if (List.class.isAssignableFrom(clazz)) {
            return (RowProcessor<T>) new Row2ListProcessor();
        }
        if (Map.class.isAssignableFrom(clazz)) {
            return (RowProcessor<T>) new Row2MapProcessor(fields);
        }
        return new Row2BeanProcessor<>(clazz, fields);
    }
}
