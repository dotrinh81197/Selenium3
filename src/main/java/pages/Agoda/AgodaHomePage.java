package pages.Agoda;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
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
    private final SelenideElement datePickerPopup = $x("//div[contains(@class,'Popup__content')]");
    private final SelenideElement datePickerCheckInField = $x("//div[@data-selenium='checkInBox']");
    private final SelenideElement occupancySelectorPanel = $x("//div[@data-selenium='occupancy-selector-panel']");

    @Step("Open Agoda Home Page")
    public void openAgodaHomePage() {
        open("/", AgodaHomePage.class);
    }

    @Step("Enter destination: {place}")
    public AgodaHomePage enterDestination(String place) {
        searchDestinationInput.shouldBe(Condition.visible).setValue(place);
        SelenideElement firstSuggestion = $("[data-element-name='search-box-sub-suggestion']").shouldBe(Condition.visible);
        firstSuggestion.click();
        return this;
    }

    private void selectDateInCalendar(LocalDate targetDate) {
        String datePicker = "//span[@data-selenium-date='%s']";
        String xpath = String.format(datePicker, targetDate);
        $x(xpath).shouldBe(Condition.visible).click();
    }

    @Step("Select dates: check-in {checkInDate}, check-out {checkOutDate}")
    public AgodaHomePage selectDates(LocalDate checkInDate, LocalDate checkOutDate) {
        datePickerCheckInField.scrollIntoView(false);

        if (!datePickerPopup.is(Condition.visible, Duration.ofSeconds(4))) {
            datePickerCheckInField.click();
            datePickerPopup.should(Condition.exist)
                    .shouldBe(Condition.visible);
        }

        selectDateInCalendar(checkInDate);
        selectDateInCalendar(checkOutDate);
        datePickerPopup.shouldBe(Condition.hidden);
        return this;
    }

    @Step("Set family travelers: rooms {targetRooms}, adults {targetAdults}")
    public void setFamilyTravelers(int targetRooms, int targetAdults) {
        selectTraveler();
        selectRooms(targetRooms);
        selectAdults(targetAdults);
        selectTraveler(); // Click done / close
    }

    @Step("Select traveler dropdown")
    public void selectTraveler() {
        if (!occupancySelectorPanel.is(Condition.visible, Duration.ofSeconds(4))) {
            travelerDropdown.shouldBe(Condition.visible).click();
        }
    }

    @Step("Select rooms: {targetRooms}")
    public void selectRooms(int targetRooms) {
        for (int i = 1; i < targetRooms; i++) {
            roomsIncreaseButton.shouldBe(Condition.visible).click();
        }
    }

    @Step("Select adults: {targetAdults}")
    public void selectAdults(int targetAdults) {
        for (int i = 1; i < targetAdults; i++) {
            adultsIncreaseButton.shouldBe(Condition.visible).click();
        }
    }

    @Step("Click search button")
    public AgodaSearchResultsPage clickSearchButton() {
        searchButton.shouldBe(Condition.visible).click();
        return page(AgodaSearchResultsPage.class);
    }

    @Step("Search hotel: destination {place}, check-in {checkInDate}, check-out {checkOutDate}, rooms {targetRooms}, adults {targetAdults}")
    public void searchHotel(String place, LocalDate checkInDate, LocalDate checkOutDate, int targetRooms, int targetAdults) {
        enterDestination(place);
        selectDates(checkInDate, checkOutDate);
        setFamilyTravelers(targetRooms, targetAdults);
        clickSearchButton();
    }
}
