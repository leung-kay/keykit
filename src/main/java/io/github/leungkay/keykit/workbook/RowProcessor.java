package io.github.leungkay.keykit.workbook;

public interface RowProcessor<T> {
    void push(int index, Object data);

    T pull();
}
