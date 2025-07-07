package utils;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class ExcelUtils {
    /**
     * Load an Excel file and map each row to a custom object using the provided mapper.
     *
     * @param filePath path to the Excel file
     * @param mapper   a function that converts a Row to a desired object type
     * @param <T>      the type of object you want to return (GameData, UserData, etc.)
     * @return List of objects mapped from Excel rows
     */
    public static <T> List<T> loadExcelData(String filePath, Function<Row, T> mapper) {
        List<T> dataList = new ArrayList<>();
        try (InputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            boolean firstRow = true;

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (firstRow) { // Skip header row
                    firstRow = false;
                    continue;
                }
                T data = mapper.apply(row);
                if (data != null) {
                    dataList.add(data);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }
}
