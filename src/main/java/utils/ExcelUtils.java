package utils;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
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

            if (rowIterator.hasNext()) {
                rowIterator.next(); // Skip header row
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                T data = mapper.apply(row);
                if (data != null) {
                    dataList.add(data);
                }
            }

        } catch (IOException e) {
            throw new ExcelProcessingException("Error reading the Excel file: " + filePath, e);
        } catch (RuntimeException e) {
            throw new ExcelProcessingException("Unexpected error while processing the Excel file: " + filePath, e);
        }
        return dataList;
    }
}

/**
 * Custom unchecked exception for errors during Excel file processing.
 */
class ExcelProcessingException extends RuntimeException {
    public ExcelProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}

