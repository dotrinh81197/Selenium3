package pages.Agoda;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.BasePage;
import utils.WindowUtils;

import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.page;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static utils.MoneyUtils.formatVND;


public class AgodaSearchResultsPage extends BasePage {

    private final static Logger log = LoggerFactory.getLogger(AgodaSearchResultsPage.class);

    // --- Locators for elements on the Search Results Page ---
    private final ElementsCollection hotelListings = $$x("(//li[@data-selenium='hotel-item'])");
    private final SelenideElement lowestPriceOption = $("[data-element-name='search-sort-price']");
    private final SelenideElement minPriceFilterTextbox = $("#price_box_0");
    private final SelenideElement maxPriceFilterTextbox = $("#price_box_1");
    private final SelenideElement minPriceSlider = $x("//div[@id='SideBarLocationFilters']//div[contains(@class,'rc-slider-handle-1')]");
    private final SelenideElement maxPriceSlider = $x("//div[@id='SideBarLocationFilters']//div[contains(@class,'rc-slider-handle-2')]");

    public ElementsCollection getHotelListings() {
        return hotelListings;
    }

    @Step("Verify that at least {expectedHotelsCount} hotels are displayed for destination: {expectedDestination}")
    public void verifySearchResultsDisplayed(int expectedHotelsCount, String expectedDestination) {
        // Wait for at least the expected number of hotel items to be visible
        hotelListings.shouldHave(CollectionCondition.sizeGreaterThanOrEqual(expectedHotelsCount));

        for (int i = 0; i < expectedHotelsCount; i++) {
            SelenideElement areaCity = hotelListings.get(i).find(By.xpath(".//div[@data-selenium='area-city']"));
            areaCity.scrollIntoView(true);
            areaCity.shouldBe(Condition.visible);
            String areaCityText = areaCity.getText();
            assertTrue(areaCityText.toLowerCase().contains(expectedDestination.toLowerCase()), "Hotel name should contain '" + expectedDestination + "' for hotel: " + areaCityText);
        }
    }

    /**
     * Applies the "Lowest Price" sort option.
     */
    @Step("Sort search results by lowest price")
    public void sortByLowestPrice() {
        lowestPriceOption.scrollIntoView(false);
        lowestPriceOption.shouldBe(Condition.visible).click(); // Click on "Lowest Price" option
    }

    @Step("Verify hotels are sorted by lowest price for destination: {expectedDestination} (checking {countHotel} valid hotels)")
    public void verifyLowestPriceSortOrder(String expectedDestination, int expectedHotelsCount) {
        hotelListings.shouldHave(CollectionCondition.sizeGreaterThanOrEqual(expectedHotelsCount));

        double previousPrice = -1;
        int validChecked = 0;
        int index = 0;

        while (validChecked < expectedHotelsCount) {
            // Avoid infinite loop if not enough valid hotels
            if (index >= hotelListings.size()) {
                throw new AssertionError(
                        String.format("Expected at least %d valid (non-sold-out) hotels, but found only %d after checking %d total.",
                                expectedHotelsCount, validChecked, index)
                );
            }

            SelenideElement hotelItem = hotelListings.get(index);
            hotelItem.scrollIntoView(true);
            if (isSoldOut(hotelItem)) continue;
            index++;

            verifyHotelDestination(hotelItem, expectedDestination);
            double currentPrice = extractPrice(hotelItem);
            if (previousPrice != -1) {
                assertTrue(
                        currentPrice >= previousPrice,
                        "Price order error at hotel " + (validChecked + 1) + ": " + currentPrice + " < " + previousPrice);
            }

            previousPrice = currentPrice;
            validChecked++;
            log.info("Valid Hotel #{}: Price={}", validChecked, currentPrice);
        }
    }

    @Step("Submit filter information with min price: {0}, max price: {1}, star rating: {2}")
    public void submitFilterInfo(int minPrice, int maxPrice, String starRating) {
        minPriceFilterTextbox.setValue(String.valueOf(minPrice));
        maxPriceFilterTextbox.setValue(String.valueOf(maxPrice));
        String starRatingCheckbox = "//span[.='%s-Star rating']//ancestor::label//input";
        SelenideElement starRatingElement = $x(String.format(starRatingCheckbox, starRating));
        starRatingElement.click();
    }

    @Step("Verify filter is highlighted with min price: {0}, max price: {1}, star rating: {2}")
    public void verifyFilterHighlighted(int minPrice, int maxPrice, String star) {
        SelenideElement starRatingElement = $x(String.format("//div[@id='SideBarLocationFilters']//legend[@id='filter-menu-RecentFilters']//following-sibling::ul//li//label[@data-element-value='%s']//div//div//input", star));
        starRatingElement.shouldBe(Condition.selected);
        verifyPriceSliderRange(minPrice, maxPrice);
    }

    @Step("Verify filter price range is highlighted with min price: {0}, max price: {1}")
    public void verifyPriceSliderRange(int minPrice, int maxPrice) {
        minPriceSlider.shouldHave(Condition.attribute("aria-valuetext", formatVND(minPrice)), defaultTimeout);
        maxPriceSlider.shouldHave(Condition.attribute("aria-valuetext", formatVND(maxPrice)), defaultTimeout);
    }

    @Step("Verify filter results for destination: {0}, price range: {1} - {2}, stars: {3}, expected count: {4}")
    public void verifyFilterResult(String expectedDestination, int minPrice, int maxPrice, String expectedStars, int expectedCount) {
        hotelListings.shouldHave(CollectionCondition.sizeGreaterThanOrEqual(expectedCount));

        int verifiedCount = 0;
        int index = 0;

        while (verifiedCount < expectedCount) {
            if (index >= hotelListings.size()) {
                throw new AssertionError("Expected at least " + expectedCount + " valid hotels, but found only " + verifiedCount);
            }

            SelenideElement hotelItem = hotelListings.get(index);
            index++;
            hotelItem.scrollIntoView(true);
            if (isSoldOut(hotelItem)) continue;

            verifyHotelDestination(hotelItem, expectedDestination);
            verifyHotelPriceInRange(hotelItem, minPrice, maxPrice);
            verifyHotelStars(hotelItem, expectedStars);

            verifiedCount++;
        }
    }

    @Step("Verify hotel destination matches expected: {1}")
    public void verifyHotelDestination(SelenideElement hotelItem, String expectedDestination) {
        SelenideElement hotelAreaElement = hotelItem.find(By.xpath(".//div[@data-selenium='area-city']"));
        hotelAreaElement.scrollIntoView(true).shouldBe(Condition.visible);
        String hotelAreaText = hotelAreaElement.getText();
        assertTrue(hotelAreaText.toLowerCase().contains(expectedDestination.toLowerCase()), "Hotel destination mismatch: " + hotelAreaText);
    }

    @Step("Verify hotel price is within range: {1} - {2}")
    public void verifyHotelPriceInRange(SelenideElement hotelItem, int minPrice, int maxPrice) {
        double price = extractPrice(hotelItem);
        assertTrue(price >= minPrice && price <= maxPrice, "Hotel price out of expected range: " + price);
    }


    @Step("Verify hotel stars match expected: {1}")
    public void verifyHotelStars(SelenideElement hotelItem, String expectedStars) {
        SelenideElement starElement = hotelItem.find(By.xpath(String.format(".//div[@data-testid='rating-container']//span[.='%s stars out of 5']", expectedStars)));
        starElement.shouldBe(Condition.visible, defaultTimeout);
        String actualStars = starElement.getText().split(" ")[0];
        assertEquals(actualStars, expectedStars);
    }

    private double extractPrice(SelenideElement hotel) {
        SelenideElement price = hotel.$x(".//span[@data-selenium='display-price']");
        price.shouldBe(Condition.visible, defaultTimeout).scrollIntoView(true);
        String text = price.getText().replaceAll("[^\\d.]", "");
        return Double.parseDouble(text);
    }

    private boolean isSoldOut(SelenideElement hotel) {
        return hotel.$x(".//span[@data-selenium='sold-out-message']").exists();
    }

    @Step("Reset price filter to default values")
    public void resetPriceFilter() {
        minPriceSlider.scrollIntoView(false);

        SelenideElement track = $(".rc-slider-rail");
        int trackWidth = track.getSize().width;

        WebDriver driver = WebDriverRunner.getWebDriver();
        Actions actions = new Actions(driver);

        // Click near left edge for min
        actions.moveToElement(track, -trackWidth / 2 + 1, 0).click().perform();

        // Click near right edge for max
        actions.moveToElement(track, trackWidth / 2 - 1, 0).click().perform();
    }

    @Step("Verify price slider is reset")
    public void verifyPriceSliderReset() {
        String minNow = minPriceSlider.getAttribute("aria-valuenow");
        String minExpected = minPriceSlider.getAttribute("aria-valuemin");

        String maxNow = maxPriceSlider.getAttribute("aria-valuenow");
        String maxExpected = maxPriceSlider.getAttribute("aria-valuemax");

        assertEquals(minNow, minExpected, "Min slider is not reset to min value");
        assertEquals(maxNow, maxExpected, "Max slider is not reset to max value");
    }

    @Step("Select hotel at index {0} from the search results")
    public AgodaHotelDetailPage selectHotelByIndex(int index) {
        if (index <= 0 || index > hotelListings.size()) {
            throw new IllegalArgumentException("Invalid hotel index: " + index);
        }

        SelenideElement hotel = hotelListings.get(index - 1);
        hotel.shouldBe(Condition.visible, defaultTimeout);
        hotel.scrollTo().click();
        Selenide.switchTo().window(2);

        return page(AgodaHotelDetailPage.class);
    }

    @Step("Filter hotels by facility: {facility}")
    public void filterFacilities(String facility) {
        String propertyFacilitiesCheckbox = "//legend[@id='filter-menu-Facilities']//following-sibling::ul//label[@data-component='search-filter-hotelfacilities']//span[@data-selenium='filter-item-text' and .='%s']";
        SelenideElement facilityCheckbox = $x(String.format(propertyFacilitiesCheckbox, facility));
        if (!facilityCheckbox.isSelected()) {
            facilityCheckbox.scrollIntoView(true).shouldBe(Condition.visible).click();
            getHotelListings().shouldHave(CollectionCondition.sizeGreaterThan(0), defaultTimeout);
        } else {
            log.info("Facility filter already applied");
        }
    }

    @Step("Get hotel name by index: {i}")
    public String getHotelNameByIndex(int i) {
        if (i < 1 || i > hotelListings.size()) {
            throw new IllegalArgumentException("Invalid hotel index: " + i);
        }
        SelenideElement hotelItem = hotelListings.get(i - 1);

        hotelItem.scrollIntoView(true);
        SelenideElement hotelNameElement = hotelItem.$x(".//div[@data-element-name='property-card-info']//h3[@data-selenium='hotel-name']");
        hotelNameElement.shouldBe(Condition.visible, defaultTimeout);
        return hotelNameElement.getText();
    }

    @Step("Get first available (not sold out) hotel name")
    public Map.Entry<Integer, String> getFirstAvailableHotelName() {
        for (int i = 1; i <= hotelListings.size(); i++) {
            String name = getHotelNameByIndex(i);
            SelenideElement hotelItem = hotelListings.get(i - 1);

            if (isSoldOut(hotelItem)) {
                continue;
            }
            return Map.entry(i, name);
        }
        throw new RuntimeException("No available hotel found.");
    }

    private void showDetailReviewPoint(int index) {
        ElementsCollection hotelReviewPointElements = $$x(
                "//div[@class='ReviewWithDemographic']"
        );

        // Ensure index is valid
        if (index <= 0 || index > hotelReviewPointElements.size()) {
            throw new IllegalArgumentException("Invalid index: " + index);
        }

        hotelReviewPointElements.get(index - 1)
                .scrollIntoView(false)
                .hover();

        SelenideElement popup = $x("//div[@data-selenium='demographics-review-container']");
        popup.shouldBe(Condition.visible, defaultTimeout);
    }

    private List<String> getDetailReviewPoint() {
        ElementsCollection labels = $$x("//span[@data-selenium='review-name']");
        labels.first().shouldBe(Condition.visible);
        return labels.texts();
    }

    @Step("Show and get review point labels for hotel at index: {index}")
    public List<String> showAndGetReviewPopup(int index) {
        showDetailReviewPoint(index);
        return getDetailReviewPoint();
    }

    @Step("Go to hotel detail page: {hotelName}")
    public void gotoDetail(String hotelName) {
        $$x("//h3[@data-selenium='hotel-name']")
                .findBy(Condition.text(hotelName))
                .click();
        Selenide.switchTo().window(3);
        WindowUtils.closeOtherWindows();
    }
}
