package utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

public class DateTimeUtils {

    public static String getNextFriday(String dateFormat) {
        LocalDate today = LocalDate.now();
        LocalDate nextFriday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        return nextFriday.format(formatter);
    }

    public static String getDateFromSpecificDate(String inputDate, int daysToAdd, String dateFormat) {
        LocalDate startDate = LocalDate.parse(inputDate, DateTimeFormatter.ofPattern(dateFormat));
        LocalDate calculatedDate = startDate.plusDays(daysToAdd);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        return calculatedDate.format(formatter);
    }

}
