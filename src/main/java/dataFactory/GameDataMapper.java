package dataFactory;

import data.GameData;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.Iterator;
import java.util.function.Function;

/**
 * GameDataMapper is a function that maps an Excel Row to a GameData object.
 * It extracts the title, age, and price from the first three cells of the row.
 */
public class GameDataMapper implements Function<Row, GameData> {
    @Override
    public GameData apply(Row row) {
        Iterator<Cell> cells = row.cellIterator();
        return new GameData(
                getNextCellValue(cells),
                getNextCellValue(cells),
                getNextCellValue(cells)
        );
    }

    private String getNextCellValue(Iterator<Cell> cells) {
        return cells.hasNext() ? cells.next().toString().trim() : "";
    }
}
