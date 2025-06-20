package pages.Agoda;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.BasePage;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$x;
import static org.testng.Assert.assertTrue;


public class AgodaSearchResultsPage extends BasePage {

    private final static Logger log = LoggerFactory.getLogger(AgodaSearchResultsPage.class);

    // --- Locators for elements on the Search Results Page ---
    private final ElementsCollection hotelListings = $$x("(//li[@data-selenium='hotel-item'])"); // Collection of hotel elements
    private final SelenideElement lowestPriceOption = $("[data-element-name='search-sort-price']"); // Option for Lowest Price

    @Step("Verify that at least {expectedMinHotels} hotels are displayed for destination: {expectedDestination}")
    public void verifySearchResultsDisplayed(int expectedMinHotels, String expectedDestination) {
        // Wait for at least the expected number of hotel items to be visible
        hotelListings.shouldHave(CollectionCondition.sizeGreaterThanOrEqual(expectedMinHotels));

        // Verify destination in the first few hotel names (optional, but good for validation)
        for (int i = 0; i < Math.min(expectedMinHotels, hotelListings.size()); i++) {
            SelenideElement areaCity = hotelListings.get(i).find(By.xpath(".//div[@data-selenium='area-city']"));
            areaCity.scrollIntoView(true);
            areaCity.shouldBe(Condition.visible);
            String areaCityText = areaCity.getText();
            // Perform a case-insensitive check for destination presence
            assertTrue(areaCityText.toLowerCase().contains(expectedDestination.toLowerCase()),
                    "Hotel name should contain '" + expectedDestination + "' for hotel: " + areaCityText);
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

    @Step("Verify hotels are sorted by lowest price for destination: {expectedDestination}")
    public void verifyLowestPriceSortOrder(String expectedDestination) {
        // Ensure enough hotels to validate sorting
        hotelListings.shouldHave(CollectionCondition.sizeGreaterThanOrEqual(5));

        double previousPrice = -1;

        for (int i = 0; i < 5; i++) {
            SelenideElement hotelItem = hotelListings.get(i);
            SelenideElement hotelAreaElement = hotelItem.$x(".//div[@data-selenium='area-city']");
            SelenideElement soldOutMessageElement = hotelItem.$x(".//span[@data-selenium='sold-out-message']");

            // Check destination
            hotelAreaElement.shouldBe(Condition.visible);
            String hotelAreaText = hotelAreaElement.getText();
            assertTrue(hotelAreaText.toLowerCase().contains(expectedDestination.toLowerCase()),
                    "Hotel destination mismatch at hotel " + (i + 1) + ": " + hotelAreaText);

            // Skip sold-out hotels from price check
            if (soldOutMessageElement.is(Condition.visible, defaultTimeout)) {
                log.info("Hotel {} is sold out. Skipping price check.", i + 1);
                continue;
            }

            SelenideElement priceElement = hotelItem.$x(".//span[@data-selenium='display-price']");
            priceElement.shouldBe(Condition.visible, defaultTimeout);
            priceElement.scrollIntoView(true);

            String priceText = priceElement.getText().replaceAll("[^\\d.]", "");
            double currentPrice = Double.parseDouble(priceText);

            if (previousPrice != -1) {
                assertTrue(currentPrice >= previousPrice,
                        "Price order error at hotel " + (i + 1) + ": " + currentPrice + " < " + previousPrice);
            }

            previousPrice = currentPrice;
            log.info("Hotel {}: {} - Price: {}", i + 1, hotelAreaText, currentPrice);
        }
    }

}
