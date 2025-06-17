package tests;

import com.codeborne.selenide.Selenide;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.Agoda.AgodaHomePage;
import pages.Agoda.AgodaSearchResultsPage;
import utils.DateTimeUtils;

import static io.qameta.allure.Allure.step;

public class AgodaSearchAndFilterTest extends AgodaBaseTest {

    private AgodaHomePage agodaHomePage;
    private AgodaSearchResultsPage agodaSearchResultsPage;

    @BeforeMethod
    void setup() {

        agodaHomePage = new AgodaHomePage();
        agodaSearchResultsPage = new AgodaSearchResultsPage();

        step("Navigate to home page");
        agodaHomePage.openAgodaHomePage();
    }

    @Test(description = "Search and filter hotel successfully")
    void TC02_Search_Filter_Hotel() {
        String place = "Da Nang";
        int targetRooms = 2;
        int targetAdults = 4;
        String dateFormat = "yyyy-MM-dd";

        String checkInDate = DateTimeUtils.getNextFriday(dateFormat); // Next Friday
        String checkOutDate = DateTimeUtils.getDateFromSpecificDate(checkInDate, 3, dateFormat); // 3 days from next Friday

        step("Step: Searching for: " + place +
                ", Check-in: " + checkInDate +
                ", Check-out: " + checkOutDate +
                ", Rooms: " + targetRooms +
                ", Adults: " + targetAdults);

        agodaHomePage
                .enterDestination(place)
                .selectDates(checkInDate, checkOutDate)
                .setFamilyTravelers(targetRooms, targetAdults);

        agodaSearchResultsPage = agodaHomePage.clickSearchButton();

        Selenide.switchTo().window(1);

        step("Step 2: Search Result displayed correctly.");
        agodaSearchResultsPage.verifySearchResultsDisplayed(5, place);

        String minPrice = "500000";
        String maxPrice = "1000000";
        String star = "3";
        step("Step 3: Filter the hotels with the following info:");
        agodaSearchResultsPage.submitFilterInfo(minPrice, maxPrice, star);

        step("Verify the price and start filtered is highlighted");
        agodaSearchResultsPage.verifyFilterHighlighted(minPrice, maxPrice, star);

        step("Verify Search Result is displayed correctly with first 5 hotels ");
        agodaSearchResultsPage.verifyFilterResult(place, minPrice, maxPrice, star);

    }
}
