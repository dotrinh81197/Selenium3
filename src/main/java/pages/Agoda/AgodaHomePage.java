package pages.Agoda;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import pages.BasePage;

import java.time.LocalDate;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.page;
import static com.codeborne.selenide.Selenide.switchTo;

public class AgodaHomePage extends BasePage {

    private final SelenideElement googlePopupIframe = $("#credential_picker_container iframe");
    private final SelenideElement googlePopupCloseButton = $("#close");

    // --- Locators for elements on the Agoda Home Page ---
    private final SelenideElement currencyButton = $x("//div[@data-element-name='currency-container-selected-currency']");
    private final SelenideElement changeCurrencyModal = $x("//div[@data-element-name='currency-container-modal']");
    private final SelenideElement searchDestinationInput = $("#textInput"); // Search input field
    private final SelenideElement searchButton = $x("//button[@data-selenium='searchButton']"); // Main search button
    private final SelenideElement travelerDropdown = $x("//div[@data-selenium='occupancyBox']"); // Traveler/Guest dropdown
    private final SelenideElement roomsIncreaseButton = $x("//div[@data-selenium='occupancyRooms']//button[@data-selenium='plus']"); // Button to increase rooms
    private final SelenideElement adultsIncreaseButton = $x("//div[@data-selenium='occupancyAdults']//button[@data-selenium='plus']"); // Button to increase adults
    private final SelenideElement datePickerPopup = $x("//div[@class='DayPicker']");
    private final SelenideElement datePickerCheckInField = $x("//div[@data-selenium='checkInBox']");
    private final SelenideElement occupancySelectorPanel = $x("//div[@data-selenium='occupancy-selector-panel']");
    private final SelenideElement defaultAdultsLabel = $x("//div[@data-selenium='desktop-occ-adult-value']");

    @Step("Open Agoda Home Page")
    public void openAgodaHomePage() {
        open("/", AgodaHomePage.class);
        closeGooglePopupIfVisible();
    }

    @Step("Select currency")
    public void selectCurrency(String localeCurrency) {
        currencyButton.shouldBe(Condition.visible).click();
        changeCurrencyModal.shouldBe(Condition.visible);
        SelenideElement optionCurrency = $x(String.format("//li[@data-element-name='currency-popup-menu-list-item']//p[.='%s']//ancestor::div[@role='checkbox']", localeCurrency));

        if (Boolean.parseBoolean(optionCurrency.getAttribute("aria-checked"))) {
            $x("//div[@data-element-name='currency-container-modal']//button").click();
        } else {
            optionCurrency.shouldBe(Condition.visible).click();
        }

        changeCurrencyModal.shouldBe(Condition.hidden, defaultTimeout);

    }

    @Step("Close Google login popup if visible")
    private void closeGooglePopupIfVisible() {
        if (googlePopupIframe.is(Condition.exist, defaultTimeout)) {
            switchTo().frame(googlePopupIframe);
            googlePopupCloseButton.shouldBe(Condition.visible, defaultTimeout).click();
            switchTo().defaultContent();
        }
    }

    @Step("Enter destination: {place}")
    public void enterDestination(String place) {
        searchDestinationInput.shouldBe(Condition.visible).setValue(place);
        SelenideElement firstSuggestion = $("[data-element-name='search-box-sub-suggestion']").shouldBe(Condition.visible);
        firstSuggestion.click();
    }

    private void selectDateInCalendar(LocalDate targetDate) {
        String datePicker = "//span[@data-selenium-date='%s']";
        String xpath = String.format(datePicker, targetDate);
        $x(xpath).shouldBe(Condition.visible).click();
    }

    @Step("Select dates: check-in {checkInDate}, check-out {checkOutDate}")
    public AgodaHomePage selectDates(LocalDate checkInDate, LocalDate checkOutDate) {
        datePickerCheckInField.scrollIntoView(true);

        if (!datePickerPopup.is(Condition.visible, defaultTimeout)) {
            datePickerCheckInField.click();
            datePickerPopup.should(Condition.exist)
                    .shouldBe(Condition.visible);
        }

        selectDateInCalendar(checkInDate);
        selectDateInCalendar(checkOutDate);
        datePickerPopup.shouldBe(Condition.hidden, defaultTimeout); // Ensure the date picker closes after selection
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
        if (!occupancySelectorPanel.is(Condition.visible, defaultTimeout)) {
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
        int count = Integer.parseInt(defaultAdultsLabel.getText()); // Get current number of adults
        for (int i = count; i < targetAdults; i++) {
            adultsIncreaseButton.shouldBe(Condition.visible).click();
        }
    }

    @Step("Click search button")
    public AgodaSearchResultsPage clickSearchButton() {
        searchButton.shouldBe(Condition.visible).click();

        switchTo().window(1);

        // Wait for the hotel list (or a reliable element)
        AgodaSearchResultsPage resultsPage = page(AgodaSearchResultsPage.class);
        resultsPage.getHotelListings().shouldHave(CollectionCondition.sizeGreaterThan(0));

        return resultsPage;
    }

    @Step("Search hotel: destination {place}, check-in {checkInDate}, check-out {checkOutDate}, rooms {targetRooms}, adults {targetAdults}")
    public AgodaSearchResultsPage searchHotel(String place, LocalDate checkInDate, LocalDate checkOutDate, int targetRooms, int targetAdults) {
        enterDestination(place);
        selectDates(checkInDate, checkOutDate);
        setFamilyTravelers(targetRooms, targetAdults);
        return clickSearchButton();
    }
}
