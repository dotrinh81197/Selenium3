package pages.Agoda;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import pages.BasePage;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static utils.MoneyUtils.formatVND;


public class AgodaSearchResultsPage extends BasePage {

    // --- Locators for elements on the Search Results Page ---
    private final ElementsCollection hotelListings = $$x("(//li[@data-selenium='hotel-item'])"); // Collection of hotel elements
    private final SelenideElement lowestPriceOption = $("[data-element-name='search-sort-price']");// Option for Lowest Price
    private final SelenideElement minPriceFilterTextbox = $("#price_box_0");
    private final SelenideElement maxPriceFilterTextbox = $("#price_box_1");
    private final SelenideElement minPriceSlider = $x("//div[@id='SideBarLocationFilters']//div[@aria-label='MIN']");
    private final SelenideElement maxPriceSlider = $x("//div[@id='SideBarLocationFilters']//div[@aria-label='MAX']");


    private final String starRatingCheckbox = "//span[.='%s-Star rating']//ancestor::label//input";

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
        System.out.println("Search results displayed correctly for destination: " + expectedDestination);
    }

    /**
     * Applies the "Lowest Price" sort option.
     */
    public void sortByLowestPrice() {
        lowestPriceOption.scrollIntoView(false);
        lowestPriceOption.shouldBe(Condition.visible).click(); // Click on "Lowest Price" option
    }

    public void verifyLowestPriceSortOrder(String expectedDestination) {
        // Ensure at least 5 hotels are present to verify order
        hotelListings.shouldHave(com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual(5));

        double previousPrice = 0; // Initialize with 0 to ensure the first price is higher
        for (int i = 1; i < 6; i++) { // Check the first 5 hotels
            SelenideElement hotelItem = hotelListings.get(i);
            SelenideElement hotelAreaElement = hotelItem.find(By.xpath(".//div[@data-selenium='area-city']"));
            SelenideElement soldOutMessageElement = hotelItem.find(By.xpath(".//span[@data-selenium='sold-out-message']"));

            if (soldOutMessageElement.isDisplayed()) {
                return;
            }

            SelenideElement priceElement = $$x("//span[@data-selenium='display-price']").get(i);

            hotelAreaElement.shouldBe(Condition.visible);
            priceElement.shouldBe(Condition.visible, Duration.ofSeconds(15));

            priceElement.scrollIntoView(true);

            String hotelAreaText = hotelAreaElement.getText();
            String priceText = priceElement.getText().replaceAll("[^\\d.]", ""); // Remove currency symbols, commas etc.

            // Re-verify destination
            assertTrue(hotelAreaText.toLowerCase().contains(expectedDestination.toLowerCase()),
                    "Hotel destination is incorrect for hotel: " + hotelAreaText);

            // Parse price and verify order
            double currentPrice = Double.parseDouble(priceText);
            assertTrue(currentPrice >= previousPrice,
                    "Hotels are not sorted by lowest price in correct order. Issue at hotel " + (i + 1) +
                            ": Current price " + currentPrice + " was not >= previous price " + previousPrice);
            previousPrice = currentPrice;
            System.out.println("Hotel " + (i + 1) + ": " + hotelAreaText + " - Price: " + currentPrice);
        }
    }

    public void submitFilterInfo(String minPrice, String maxPrice, String starRating) {
        minPriceFilterTextbox.setValue(minPrice);
        maxPriceFilterTextbox.setValue(maxPrice);
        SelenideElement starRatingElement = $x(String.format(starRatingCheckbox, starRating));
        starRatingElement.click();
        Selenide.Wait();
    }

    public void verifyFilterHighlighted(String minPrice, String maxPrice, String star) {

        SelenideElement starRatingElement = $x(String.format("//div[@id='SideBarLocationFilters']//legend[@id='filter-menu-RecentFilters']//following-sibling::ul//li//label[@data-element-value='%s']//div//div//input", star));
        starRatingElement.shouldBe(Condition.selected);

        assertEquals(minPriceSlider.getAttribute("aria-valuetext"), formatVND(Integer.parseInt(minPrice)));
        assertEquals(maxPriceSlider.getAttribute("aria-valuetext"), formatVND(Integer.parseInt(maxPrice)));
    }

    public void verifyFilterResult(String expectedDestination, String minPrice, String maxPrice, String expectedStars) {
        hotelListings.shouldHave(CollectionCondition.sizeGreaterThanOrEqual(5));

        int verifiedCount = 0;
        int currentIndex = 0;

        while (verifiedCount < 5 && currentIndex < hotelListings.size()) {
            SelenideElement hotelItem = hotelListings.get(currentIndex++);

            // --- Sold out check ---
            SelenideElement soldOutMessageElement = hotelItem.find(By.xpath(".//span[@data-selenium='sold-out-message']"));
            if (soldOutMessageElement.exists()) {
                // skip sold out, don't count it
                continue;
            }

            // --- Destination ---
            SelenideElement hotelAreaElement = hotelItem.find(By.xpath(".//div[@data-selenium='area-city']"));
            hotelAreaElement.scrollIntoView(true)
                    .shouldBe(Condition.visible);
            String hotelAreaText = hotelAreaElement.getText();
            assertTrue(hotelAreaText.toLowerCase().contains(expectedDestination.toLowerCase()),
                    "Hotel destination mismatch: " + hotelAreaText);

            // --- Price ---
            SelenideElement priceElement = hotelItem.find(By.xpath(".//span[@data-selenium='display-price']"));
            priceElement.shouldBe(Condition.visible, Duration.ofSeconds(10));
            priceElement.scrollIntoView(true);
            String priceText = priceElement.getText().replaceAll("[^\\d.]", "");
            double price = Double.parseDouble(priceText);
            assertTrue(price >= Integer.parseInt(minPrice) && price <= Integer.parseInt(maxPrice),
                    "Hotel price out of expected range: " + price);

            // --- Star ---
            SelenideElement starElement = hotelItem.find(By.xpath(
                    String.format(".//div[@data-testid='rating-container']//span[.='%s stars out of 5']", expectedStars)
            ));
            starElement.shouldBe(Condition.visible, Duration.ofSeconds(10));
            String actualStars = starElement.getText().split(" ")[0];
            assertEquals(actualStars, expectedStars, "Star mismatch! Expected: " + expectedStars + ", got: " + actualStars);

            String log = String.format("Hotel #%d: Destination=%s | Price=%.2f | Star=%s",
                    verifiedCount + 1, hotelAreaText, price, expectedStars);
            System.out.println(log);
            Allure.addAttachment("Hotel #" + (verifiedCount + 1) + " Details", log);

            verifiedCount++;
        }

        assertTrue(verifiedCount == 5,
                "Expected to verify 5 valid hotels but found only " + verifiedCount + ". Check sold out hotels or filters.");
    }

}
