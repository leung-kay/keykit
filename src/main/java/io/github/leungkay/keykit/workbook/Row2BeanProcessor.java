package io.github.leungkay.keykit.workbook;

import lombok.NonNull;
import lombok.SneakyThrows;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Row2BeanProcessor<T> implements RowProcessor<T> {
    protected Class<T> clazz;
    protected T line;
    protected Map<String, List<Method>> beanMethods;
    protected String[] fields;

    @SneakyThrows
    public Row2BeanProcessor(@NonNull Class<T> clazz, String... fields) {
        this.clazz = clazz;
        this.line = clazz.newInstance();
        this.beanMethods = Arrays.stream(clazz.getMethods()).collect(Collectors.groupingBy(Method::getName, Collectors.toList()));
        this.fields = fields;
    }

    @Override
    public void push(int index, Object data) {
        String setterName = "set" + fields[index].toUpperCase().charAt(0) + fields[index].substring(1);
        List<Method> methods = beanMethods.get(setterName);
        methods.stream()
                .filter(method -> method.getParameterCount() == 1 && method.getParameterTypes()[0].isAssignableFrom(data.getClass()))
                .findFirst()
                .ifPresent(method-> {
                    try {
                        method.invoke(this.line, data);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        // 调用失败不处理
                    }
                });
    }

    @SneakyThrows
    @Override
    public T pull() {
        T result = line;
        line = clazz.newInstance();
        return result;
    }
}
