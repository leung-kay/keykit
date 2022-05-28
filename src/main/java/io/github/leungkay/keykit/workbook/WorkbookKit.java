package io.github.leungkay.keykit.workbook;

import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WorkbookKit {

    @SneakyThrows
    public static WorkbookTemplate build(String filePath) {
        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(filePath));
        return new WorkbookTemplate(workbook);
    }

    /**
     * @param filePath 待读取的文件路径
     * @param startX   横坐标(列)
     * @param startY   纵坐标(行)
     * @param width    读取多少列
     */
    @SuppressWarnings("rawtypes")
    @SneakyThrows
    public static List<List> read(String filePath, int startX, int startY, int width) {
        XSSFWorkbook workbook = new XSSFWorkbook(filePath);
        return internalRead(workbook, 0, startX, startY, List.class, new String[width]);
    }

    @SuppressWarnings("rawtypes")
    @SneakyThrows
    public static List<Map> read(String filePath, int startX, int startY, String... fields) {
        XSSFWorkbook workbook = new XSSFWorkbook(filePath);
        return internalRead(workbook, 0, startX, startY, Map.class, fields);
    }

    @SneakyThrows
    public static <T> List<T> read(String filePath, int startX, int startY, Class<T> clazz, String... fields) {
        XSSFWorkbook workbook = new XSSFWorkbook(filePath);
        return internalRead(workbook, 0, startX, startY, clazz, fields);
    }

    /**
     * @param file   待读取的文件
     * @param index  待读取的表单
     * @param startX 第几列开始读取，从0开始
     * @param startY 第几行开始读取，从0开始
     * @param clazz  返回数据是否用bean包装，如果是null则用List<Object>包装
     * @param fields 一共读几列
     * @return 每一行数据
     */
    private static <T> List<T> internalRead(XSSFWorkbook file, int index, int startX, int startY, @NonNull Class<T> clazz, String... fields) {
        XSSFSheet sheet = file.getSheetAt(index);
        // 最后返回读取的内容
        List<T> lines = new ArrayList<>();
        // 合并单元格区域
        List<CellRangeAddress> regions = sheet.getMergedRegions();
        // 最后一行
        int rowNum = sheet.getLastRowNum();
        // 生成多种返回值类型
        RowProcessor<T> processor = RowProcessorFactory.getRowProcessor(clazz, fields);
        // 从指定的行开始读取数据
        for (int i = startY; i <= rowNum; i++) {
            XSSFRow row = sheet.getRow(i);
            // 从指定的列开始读取数 读取不超过指定的列数
            for (int j = startX; j < startX + fields.length; j++) {
                // 默认标记成不是合并单元格区域
                boolean merged = false;
                for (CellRangeAddress region : regions) {
                    // 当前单元格是否在被合并区域当中
                    if (region.isInRange(i, j)) {
                        merged = true;
                        // 如果合并了单元格 则只有第一个格的数据被记入List 其余位置null
                        if (region.getFirstRow() == i && region.getFirstColumn() == j) {
                            processor.push(j - startX, getData(row, j));
                        } else {
                            processor.push(j - startX, null);
                        }
                        // 当前单元格只可能命中一个区域
                        break;
                    }
                }
                // 遍历过所有合并区域没有命中
                if (!merged) {
                    processor.push(j - startX, getData(row, j));
                }
            }
            lines.add(processor.pull());
        }
        return lines;
    }

    private static Object getData(XSSFRow row, int x) {
        if (row == null)
            return null;
        XSSFCell cell = row.getCell(x);
        if (cell == null)
            return null;
        switch (cell.getCellType()) {
            case BLANK:
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return cell.getNumericCellValue();
            case FORMULA:
                return cell.getCellFormula();
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case ERROR:
                return cell.getErrorCellString();
            default:
                return null;
        }
    }
}
