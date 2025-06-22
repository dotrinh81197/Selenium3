package controls;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class CalendarControl {

    private final SelenideElement root;

    private final DateTimeFormatter headerFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");

    public CalendarControl(SelenideElement root) {
        this.root = root;
    }

    public void selectDate(LocalDate targetDate) {
        YearMonth target = YearMonth.from(targetDate);

        int attempts = 0;
        int maxAttempts = 12;
        while (attempts < maxAttempts) {
            SelenideElement monthHeader = root.$$(".DayPicker-Month").first().$(".DayPicker-Caption");

            String currentMonthYear = monthHeader.getText();
            YearMonth current = YearMonth.parse(currentMonthYear, headerFormatter);

            if (current.equals(target)) {
                break;
            } else if (current.isBefore(target)) {
                SelenideElement nextButton = root.$("[aria-label='Next Month']");
                nextButton.click();
                monthHeader.shouldNotHave(Condition.exactText(currentMonthYear));
            } else {
                SelenideElement prevButton = root.$("[aria-label='Previous Month']");
                prevButton.click();
                monthHeader.shouldNotHave(Condition.exactText(currentMonthYear));
            }

            attempts++;
        }

        if (attempts == maxAttempts) {
            throw new RuntimeException("Could not reach target month: " + targetDate);
        }

        // Click the day — locate within the root
        root.$x(String.format(".//span[@data-selenium-date='%s']", targetDate)).shouldBe(Condition.visible).click();
    }
}
