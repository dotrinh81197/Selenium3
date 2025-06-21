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


}
