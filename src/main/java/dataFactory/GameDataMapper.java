package dataFactory;

import data.GameData;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.function.Function;

/**
 * GameDataMapper is a function that maps an Excel Row to a GameData object.
 * It extracts the title, age, and price from the first three cells of the row.
 * The row index is also captured for reporting purposes.
 */
public class GameDataMapper implements Function<Row, GameData> {
    @Override
    public GameData apply(Row row) {
        Cell titleCell = row.getCell(0);
        Cell ageCell = row.getCell(1);
        Cell priceCell = row.getCell(2);

        String title = titleCell != null ? titleCell.getStringCellValue().trim() : "";
        String age = ageCell != null ? ageCell.getStringCellValue().trim() : "";
        String price = priceCell != null ? priceCell.getStringCellValue().trim() : "";

        int rowIndex = row.getRowNum() + 1; // match Excel row index
        return new GameData(title, age, price, rowIndex);
    }
}
