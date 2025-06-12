package pages.Agoda;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import pages.BasePage;

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
    private final SelenideElement datePickerPopup = $x("//div[contains(@class,'Popup__content')]");
    private final SelenideElement datePickerCheckInField = $x("//div[@data-selenium='checkInBox']");
    private final SelenideElement occupancySelectorPanel = $x("//div[@data-selenium='occupancy-selector-panel']");

    public void openAgodaHomePage() {
        open("/", AgodaHomePage.class);
    }

    public AgodaHomePage enterDestination(String place) {
        searchDestinationInput.shouldBe(Condition.visible).setValue(place);
        SelenideElement firstSuggestion = $("[data-element-name='search-box-sub-suggestion']").shouldBe(Condition.visible);
        firstSuggestion.click();
        return this;
    }

    private void selectDateInCalendar(String targetDate) {
        String datePicker = "//span[@data-selenium-date='%s']";
        String xpath = String.format(datePicker, targetDate);
        $x(xpath).shouldBe(Condition.visible).click();
    }

    public AgodaHomePage selectDates(String checkInDate, String checkOutDate) {
        // Click on the check-in field to open the date picker

        if (!datePickerPopup.shouldBe(Condition.visible).isDisplayed()) {
            datePickerCheckInField.shouldBe(Condition.visible).click();
        }
        selectDateInCalendar(checkInDate);
        selectDateInCalendar(checkOutDate); // Select check-out date

        return this;
    }

    public void setFamilyTravelers(int targetRooms, int targetAdults) {
        if (!occupancySelectorPanel.shouldBe(Condition.visible).isDisplayed()) {
            travelerDropdown.shouldBe(Condition.visible).click(); // Click to open guest selector
        }

        for (int i = 1; i < targetRooms; i++) { // From 1 to targetRooms-1 clicks
            roomsIncreaseButton.shouldBe(Condition.visible).click();
        }

        // Adjust adults to targetAdults
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
