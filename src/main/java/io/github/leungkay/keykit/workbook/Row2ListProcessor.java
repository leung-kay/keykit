package io.github.leungkay.keykit.workbook;

import java.util.ArrayList;
import java.util.List;

public class Row2ListProcessor implements RowProcessor<List<Object>> {
    protected List<Object> line;

    public Row2ListProcessor() {
        this.line = new ArrayList<>();
    }

    @Override
    public void push(int index, Object data) {
        line.add(data);
    }

    @Override
    public List<Object> pull() {
        List<Object> result = line;
        line = new ArrayList<>();
        return result;
    }
}
