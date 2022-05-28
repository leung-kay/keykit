package io.github.leungkay.keykit.workbook;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class WorkbookTemplate {

    private final XSSFWorkbook workbook;
    private final XSSFSheet sheet;
    private final List<String> header;
    private final List<String> fields;
    private final List<Map<String, Object>> contents;
    private int startX;
    private int startY;

    public WorkbookTemplate(XSSFWorkbook workbook) {
        this.workbook = workbook;
        this.sheet = workbook.getSheetAt(0);
        this.header = new ArrayList<>();
        this.fields = new ArrayList<>();
        this.contents = new ArrayList<>();
    }

    /**
     * @param x      横坐标
     * @param y      纵坐标
     * @param length 列数
     * @return builder中间对象
     */
    public WorkbookTemplate header(int x, int y, int length, String... fields) {
        // 内容的横坐标相同
        this.startX = x;
        // 内容的纵坐标是下一行
        this.startY = y + 1;
        XSSFRow row = sheet.getRow(y);
        for (int i = 0; i < length; i++) {
            String header = row.getCell(x + i).getStringCellValue();
            this.header.add(header + "__" + i);
        }
        if (fields.length > 0) {
            if (fields.length != length) throw new RuntimeException();
            if (fields.length == 1) {
                header(fields[0]);
            } else {
                // 将fields字段拆成2部分传入
                String[] fieldsWithoutFirst = new String[fields.length - 1];
                System.arraycopy(fields, 1, fieldsWithoutFirst, 0, fields.length - 1);
                header(fields[0], fieldsWithoutFirst);
            }
        }
        return this;
    }

    public WorkbookTemplate header(String field, String... fields) {
        this.fields.clear();
        this.fields.add(field);
        this.fields.addAll(Arrays.asList(fields));
        return this;
    }

    public WorkbookTemplate locateContent(int x, int y) {
        this.startX = x;
        this.startY = y;
        return this;
    }

    public WorkbookTemplate content(Object content, Object... contents) {
        if (this.header.size() != contents.length + 1) throw new RuntimeException();
        Map<String, Object> contentMap = new LinkedHashMap<>();
        // header中的第一个字段匹配必传的那个content
        contentMap.put(this.header.get(0), content);
        // 从header的第二个字段开始遍历
        for (int i = 1; i < this.header.size(); i++) {
            String key = this.header.get(i);
            // contents里的第一个元素对应header中的第二个元素
            Object value = contents[i - 1];
            contentMap.put(key, value);
        }
        this.contents.add(contentMap);
        return this;
    }

    public WorkbookTemplate contents(List<Map<String, Object>> contents) {
        this.contents.addAll(contents);
        return this;
    }

    public WorkbookTemplate content(Map<String, Object> content) {
        this.contents.add(content);
        return this;
    }

    @SneakyThrows
    public WorkbookTemplate beanContents(List<?> contents) {
        for (Object obj : contents) {
            Map<String, Method> getterMethods = Arrays.stream(obj.getClass().getMethods())
                    .filter(method -> method.getName().startsWith("get"))
                    .collect(Collectors.toMap(Method::getName, method -> method, (method1, method2) -> method1.getParameterCount() == 0 ? method1 : method2));
            Map<String, Object> content = new LinkedHashMap<>();
            for (int i = 0; i < this.fields.size(); i++) {
                String field = this.fields.get(i);
                Method getter = getterMethods.get("get" + field.toUpperCase().charAt(0) + field.substring(1));
                if (getter != null)
                    content.put(String.valueOf(i), getter.invoke(obj));
            }
            content(content);
        }
        return this;
    }

    @SneakyThrows
    public byte[] export() {
        // 列
        int x = startX;
        // 行
        int y = startY;
        CellStyle firstRowStyle = null;
        List<CellStyle> firstRowCellStyles = new ArrayList<>();
        for (Map<String, Object> content : this.contents) {
            XSSFRow row;
            if ((row = sheet.getRow(y)) != null) {
                if (y == startY) {
                    //添加
                    firstRowStyle = row.getRowStyle();
                    Iterator<Cell> it = row.cellIterator();
                    while (it.hasNext()) {
                        firstRowCellStyles.add(it.next().getCellStyle());
                    }
                }
            } else {
                row = sheet.createRow(y);
                row.setRowStyle(firstRowStyle);
            }
            for (String header : this.header) {
                String[] titleAndIndex = header.split("__");
                String title = titleAndIndex[0];
                String indexStr = titleAndIndex[1];
                int index = Integer.parseInt(indexStr);
                Object value;
                XSSFCell cell;
                cell = row.createCell(x + index);
                if (firstRowCellStyles.size() > x + index) {
                    cell.setCellStyle(firstRowCellStyles.get(x + index));
                }
                if ((value = content.get(header)) != null || (value = content.get(title)) != null || (value = content.get(indexStr)) != null) {
                    if (value instanceof Double) {
                        cell.setCellValue((Double) value);
                    } else if (value instanceof Integer) {
                        cell.setCellValue((Integer) value);
                    } else if (value instanceof LocalDate) {
                        cell.setCellValue((LocalDate) value);
                    } else if (value instanceof LocalDateTime) {
                        cell.setCellValue((LocalDateTime) value);
                    } else if (value instanceof Boolean) {
                        cell.setCellValue((Boolean) value);
                    } else {
                        cell.setCellValue(value.toString());
                    }
                }
            }
            y++;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        return bos.toByteArray();
    }

    @SneakyThrows
    public void export(String targetPath) {
        FileUtils.writeByteArrayToFile(new File(targetPath), export(), false);
    }

}
