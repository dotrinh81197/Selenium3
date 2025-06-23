package pages.Agoda;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.BasePage;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static utils.MoneyUtils.formatVND;


public class AgodaSearchResultsPage extends BasePage {

    private final static Logger log = LoggerFactory.getLogger(AgodaSearchResultsPage.class);

    // --- Locators for elements on the Search Results Page ---
    @Getter
    private final ElementsCollection hotelListings = $$x("(//li[@data-selenium='hotel-item'])"); // Collection of hotel elements
    private final SelenideElement lowestPriceOption = $("[data-element-name='search-sort-price']");// Option for Lowest Price
    private final SelenideElement minPriceFilterTextbox = $("#price_box_0");
    private final SelenideElement maxPriceFilterTextbox = $("#price_box_1");
    private final SelenideElement minPriceSlider = $x("//div[@id='SideBarLocationFilters']//div[@aria-label='MIN']");
    private final SelenideElement maxPriceSlider = $x("//div[@id='SideBarLocationFilters']//div[@aria-label='MAX']");

    private final String starRatingCheckbox = "//span[.='%s-Star rating']//ancestor::label//input";

    @Step("Verify that at least {expectedHotelsCount} hotels are displayed for destination: {expectedDestination}")
    public void verifySearchResultsDisplayed(int expectedHotelsCount, String expectedDestination) {
        // Wait for at least the expected number of hotel items to be visible
        hotelListings.shouldHave(CollectionCondition.sizeGreaterThanOrEqual(expectedHotelsCount));

        // Verify destination in the first few hotel names (optional, but good for validation)
        for (int i = 0; i < Math.min(expectedHotelsCount, hotelListings.size()); i++) {
            SelenideElement areaCity = hotelListings.get(i).find(By.xpath(".//div[@data-selenium='area-city']"));
            areaCity.scrollIntoView(true);
            areaCity.shouldBe(Condition.visible);
            String areaCityText = areaCity.getText();
            // Perform a case-insensitive check for destination presence
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
        // Wait for at least some hotels to be present (even if some are sold out)
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
            SelenideElement hotelAreaElement = hotelItem.$x(".//div[@data-selenium='area-city']");
            SelenideElement soldOutMessageElement = hotelItem.$x(".//span[@data-selenium='sold-out-message']");
            SelenideElement priceElement = hotelItem.$x(".//span[@data-selenium='display-price']");

            hotelAreaElement.shouldBe(Condition.visible, defaultTimeout);
            String hotelAreaText = hotelAreaElement.getText();
            assertTrue(
                    hotelAreaText.toLowerCase().contains(expectedDestination.toLowerCase()),
                    String.format("Destination mismatch at hotel #%d: [%s] does not contain [%s]", index + 1, hotelAreaText, expectedDestination)
            );

            if (soldOutMessageElement.is(Condition.visible, defaultTimeout)) {
                log.info("Hotel #{} [{}] is sold out. Skipping price check.", index + 1, hotelAreaText);
                index++;
                continue;
            }

            priceElement.shouldBe(Condition.visible, defaultTimeout).scrollIntoView(true);
            String priceText = priceElement.getText().replaceAll("[^\\d.]", "");
            double currentPrice = Double.parseDouble(priceText);

            if (previousPrice != -1) {
                assertTrue(
                        currentPrice >= previousPrice,
                        "Price order error at hotel " + (validChecked + 1) + ": " + currentPrice + " < " + previousPrice);
            }

            previousPrice = currentPrice;
            validChecked++;
            log.info("Valid Hotel #{}: {} | Price: {}", validChecked, hotelAreaText, currentPrice);
            index++;
        }
    }

    @Step("Submit filter information with min price: {0}, max price: {1}, star rating: {2}")
    public void submitFilterInfo(String minPrice, String maxPrice, String starRating) {
        minPriceFilterTextbox.setValue(minPrice);
        maxPriceFilterTextbox.setValue(maxPrice);
        SelenideElement starRatingElement = $x(String.format(starRatingCheckbox, starRating));
        starRatingElement.click();
        Selenide.Wait();
    }

    @Step("Verify filter is highlighted with min price: {0}, max price: {1}, star rating: {2}")
    public void verifyFilterHighlighted(String minPrice, String maxPrice, String star) {

        SelenideElement starRatingElement = $x(String.format("//div[@id='SideBarLocationFilters']//legend[@id='filter-menu-RecentFilters']//following-sibling::ul//li//label[@data-element-value='%s']//div//div//input", star));
        starRatingElement.shouldBe(Condition.selected);

        assertEquals(minPriceSlider.getAttribute("aria-valuetext"), formatVND(Integer.parseInt(minPrice)));
        assertEquals(maxPriceSlider.getAttribute("aria-valuetext"), formatVND(Integer.parseInt(maxPrice)));
    }

    /**
     * Get the next valid hotel element that is not sold out.
     *
     * @param startIndex index to start searching from
     * @return SelenideElement of next valid hotel, or null if none found.
     */
    private SelenideElement getNextValidHotel(int startIndex) {
        for (int i = startIndex; i < hotelListings.size(); i++) {
            SelenideElement hotel = hotelListings.get(i);
            if (!hotel.$x(".//span[@data-selenium='sold-out-message']").exists()) {
                return hotel;
            }
        }
        return null;
    }

    @Step("Verify filter results for destination: {0}, price range: {1} - {2}, stars: {3}, expected count: {4}")
    public void verifyFilterResult(String expectedDestination, String minPrice, String maxPrice, String expectedStars, int expectedCount) {
        hotelListings.shouldHave(CollectionCondition.sizeGreaterThanOrEqual(expectedCount));

        int verifiedCount = 0;
        int index = 0;

        while (verifiedCount < expectedCount) {
            if (index >= hotelListings.size()) {
                throw new AssertionError("Expected at least " + expectedCount + " valid hotels, but found only " + verifiedCount);
            }

            SelenideElement hotelItem = hotelListings.get(index);
            index++;

            if (hotelItem.$x(".//span[@data-selenium='sold-out-message']").exists()) {
                continue; // skip sold out
            }

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
    public void verifyHotelPriceInRange(SelenideElement hotelItem, String minPrice, String maxPrice) {
        SelenideElement priceElement = hotelItem.find(By.xpath(".//span[@data-selenium='display-price']"));
        priceElement.shouldBe(Condition.visible, defaultTimeout);
        priceElement.scrollIntoView(true);
        String priceText = priceElement.getText().replaceAll("[^\\d.]", "");
        double price = Double.parseDouble(priceText);
        assertTrue(price >= Integer.parseInt(minPrice) && price <= Integer.parseInt(maxPrice), "Hotel price out of expected range: " + price);
    }


    @Step("Verify hotel stars match expected: {1}")
    public void verifyHotelStars(SelenideElement hotelItem, String expectedStars) {
        SelenideElement starElement = hotelItem.find(By.xpath(String.format(".//div[@data-testid='rating-container']//span[.='%s stars out of 5']", expectedStars)));
        starElement.shouldBe(Condition.visible, defaultTimeout);
        String actualStars = starElement.getText().split(" ")[0];
        assertEquals(actualStars, expectedStars, "Star mismatch! Expected: " + expectedStars + ", got: " + actualStars);
    }

}
