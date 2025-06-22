package pages.Agoda;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import controls.CalendarControl;
import pages.BasePage;

import java.time.Duration;
import java.time.LocalDate;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.page;

public class AgodaHomePage extends BasePage {

    // --- Locators for elements on the Agoda Home Page ---
    private final SelenideElement searchDestinationInput = $("#textInput"); // Search input field
    private final SelenideElement searchButton = $x("//button[@data-selenium='searchButton']"); // Main search button
    private final SelenideElement travelerDropdown = $x("//div[@data-selenium='occupancyBox']"); // Traveler/Guest dropdown
    private final SelenideElement roomsIncreaseButton = $x("//div[@data-selenium='occupancyRooms']//button[@data-selenium='plus']"); // Button to increase rooms
    private final SelenideElement adultsIncreaseButton = $x("//div[@data-selenium='occupancyAdults']//button[@data-selenium='plus']"); // Button to increase adults
    private final SelenideElement datePickerPopup = $x("//div[@class='DayPicker']");
    private final SelenideElement datePickerCheckInField = $x("//div[@data-selenium='checkInBox']");
    private final SelenideElement occupancySelectorPanel = $x("//div[@data-selenium='occupancy-selector-panel']");


    private final SelenideElement calendarRoot = $x("//div[@class='DayPicker']");
    private final CalendarControl calendarControl = new CalendarControl(calendarRoot);


    public void openAgodaHomePage() {
        open("/", AgodaHomePage.class);
    }

    public AgodaHomePage enterDestination(String place) {
        searchDestinationInput.shouldBe(Condition.visible).setValue(place);
        SelenideElement firstSuggestion = $("[data-element-name='search-box-sub-suggestion']").shouldBe(Condition.visible);
        firstSuggestion.click();
        firstSuggestion.shouldBe(Condition.hidden);
        return this;
    }

    private void selectDateInCalendar(LocalDate targetDate) {
        calendarControl.selectDate(targetDate);
    }

    public AgodaHomePage selectDates(LocalDate checkInDate, LocalDate checkOutDate) {
        datePickerCheckInField.scrollIntoView(false);
        // Make sure the check-in field is visible first
        datePickerCheckInField.shouldBe(Condition.visible);

        // Wait robustly: first exist, then visible
        datePickerPopup.should(Condition.exist, Duration.ofSeconds(5))
                .shouldBe(Condition.visible, Duration.ofSeconds(5));

        // If still not visible (race condition), click again
        if (!datePickerPopup.is(Condition.visible)) {
            datePickerCheckInField.click();
            datePickerPopup.should(Condition.exist, Duration.ofSeconds(5))
                    .shouldBe(Condition.visible, Duration.ofSeconds(5));
        }

        selectDateInCalendar(checkInDate);
        selectDateInCalendar(checkOutDate);
        datePickerPopup.shouldBe(Condition.hidden);
        return this;
    }

    public void setFamilyTravelers(int targetRooms, int targetAdults) {
        if (!occupancySelectorPanel.shouldBe(Condition.visible).isDisplayed()) {
            travelerDropdown.shouldBe(Condition.visible).click();
        }

        for (int i = 1; i < targetRooms; i++) {
            roomsIncreaseButton.shouldBe(Condition.visible).click();
        }

        for (int i = 2; i < targetAdults; i++) {
            adultsIncreaseButton.shouldBe(Condition.visible).click();
        }

        travelerDropdown.shouldBe(Condition.visible).click(); // Click done
    }

    public AgodaSearchResultsPage clickSearchButton() {
        searchButton.shouldBe(Condition.visible).click();
        return page(AgodaSearchResultsPage.class);
    }
}
