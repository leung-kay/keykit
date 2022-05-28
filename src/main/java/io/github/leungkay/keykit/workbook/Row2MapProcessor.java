package io.github.leungkay.keykit.workbook;

import java.util.HashMap;
import java.util.Map;

public class Row2MapProcessor implements RowProcessor<Map<String, Object>> {
    protected Map<String, Object> line;
    protected String[] fields;

    public Row2MapProcessor(String... fields) {
        this.line = new HashMap<>();
        this.fields = fields;
    }

    @Override
    public void push(int index, Object data) {
        line.put(fields[index], data);
    }

    @Override
    public Map<String, Object> pull() {
        Map<String, Object> result = line;
        line = new HashMap<>();
        return result;
    }
}
