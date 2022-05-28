package io.github.leungkay.keykit.workbook;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

@Slf4j
public class WorkbookKitTest {
    private static final String resourcePath = Objects.requireNonNull(WorkbookKitTest.class.getResource("/")).getPath();

    @Test
    @DisplayName("测试跳过表头列和表头行读取一个区域，每行返回一个List")
    public void testRead2List() {
        // 读取姓名、年龄、性别
        var result = WorkbookKit.read(resourcePath + "ReadContent.xlsx", 1, 1, 3);
        log.info(result.toString());
        Assertions.assertEquals(result.size(), 3);
        Assertions.assertIterableEquals(result.get(0), Arrays.asList("张三", 20.0D, true));
        Assertions.assertIterableEquals(result.get(1), Arrays.asList("李四", 19.0D, false));
        Assertions.assertIterableEquals(result.get(2), Arrays.asList("王五", 18.0D, true));
    }

    @Test
    @DisplayName("测试跳过表头列和表头行读取一个区域，每行返回一个Map")
    public void testRead2Map() {
        // 读取姓名、年龄、性别
        var result = WorkbookKit.read(resourcePath + "ReadContent.xlsx", 1, 1, "name", "age", "isMale");
        log.info(result.toString());
        Assertions.assertEquals(result.size(), 3);
        var row = result.get(0);
        Assertions.assertEquals(row.get("name"), "张三");
        Assertions.assertEquals(row.get("age"), 20.0);
        Assertions.assertEquals(row.get("isMale"), true);
        row = result.get(1);
        Assertions.assertEquals(row.get("name"), "李四");
        Assertions.assertEquals(row.get("age"), 19.0);
        Assertions.assertEquals(row.get("isMale"), false);
        row = result.get(2);
        Assertions.assertEquals(row.get("name"), "王五");
        Assertions.assertEquals(row.get("age"), 18.0);
        Assertions.assertEquals(row.get("isMale"), true);
    }

    @Test
    @DisplayName("测试跳过表头列和表头行读取一个区域，每行返回一个对象")
    public void testRead2Bean() {
        // 读取姓名、年龄、性别
        var result = WorkbookKit.read(resourcePath + "ReadContent.xlsx", 1, 1, PersonBean.class, "name", "age", "isMale");
        log.info(result.toString());
        Assertions.assertEquals(result.size(), 3);
        var row = result.get(0);
        Assertions.assertEquals(row.getName(), "张三");
        Assertions.assertEquals(row.getAge(), 20.0);
        Assertions.assertTrue(row.getIsMale());
        row = result.get(1);
        Assertions.assertEquals(row.getName(), "李四");
        Assertions.assertEquals(row.getAge(), 19.0);
        Assertions.assertFalse(row.getIsMale());
        row = result.get(2);
        Assertions.assertEquals(row.getName(), "王五");
        Assertions.assertEquals(row.getAge(), 18.0);
        Assertions.assertTrue(row.getIsMale());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class PersonBean {
        private String name;
        private Double age;
        private Boolean isMale;
    }

    @Test
    @DisplayName("测试将固定3行内容写到有表头和格式的单元格中")
    public void testWrite() {
        WorkbookKit.build(resourcePath + "WriteContent.xlsx")
                .header(0, 0, 4)
                .content(1, "张三", 20, true)
                .content(2, "李四", 19, true)
                .content(3, "王五", 18, true)
                .export(resourcePath + "WriteContent" + System.currentTimeMillis() + ".xlsx");
    }

    @Test
    @DisplayName("测试将固定3行内容写到有表头和格式的单元格中")
    public void testWriteBean() {
        WorkbookKit.build(resourcePath + "WriteContent.xlsx")
                .header(1, 0, 3, "name", "age", "isMale")
                .beanContents(Collections.singletonList(new PersonBean("张三", 20D, true)))
                .export(resourcePath + "WriteContent" + System.currentTimeMillis() + ".xlsx");
    }
}
