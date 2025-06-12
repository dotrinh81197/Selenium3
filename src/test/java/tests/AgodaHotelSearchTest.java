package tests;

import com.codeborne.selenide.Selenide;
import listeners.RetryAnalyzer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.Agoda.AgodaHomePage;
import pages.Agoda.AgodaSearchResultsPage;
import utils.DateTimeUtils;

import static io.qameta.allure.Allure.step;

public class AgodaHotelSearchTest extends BaseTest {

    private AgodaHomePage agodaHomePage;
    private AgodaSearchResultsPage agodaSearchResultsPage;

    @BeforeMethod
    void setup() {

        agodaHomePage = new AgodaHomePage();
        agodaSearchResultsPage = new AgodaSearchResultsPage();

        step("Navigate to home page");
        agodaHomePage.openAgodaHomePage();
    }

    @Test(description = "Search and sort hotel successfully", retryAnalyzer = RetryAnalyzer.class)
    void TC01_Search_Sort_Hotel() {

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

        step("Step 3: Hotels sorted by lowest price and destination verified.");
        agodaSearchResultsPage.sortByLowestPrice();
        agodaSearchResultsPage.verifyLowestPriceSortOrder(place);

    }


}

