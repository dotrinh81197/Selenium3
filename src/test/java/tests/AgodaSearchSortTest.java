package tests;

import com.codeborne.selenide.Selenide;
import listeners.TestListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pages.Agoda.AgodaHomePage;
import pages.Agoda.AgodaSearchResultsPage;
import utils.DateTimeUtils;

import java.time.LocalDate;

import static io.qameta.allure.Allure.step;

@Listeners(TestListener.class)
public class AgodaSearchSortTest extends AgodaBaseTest {
    LocalDate checkInDate;
    LocalDate checkOutDate;

    private AgodaHomePage agodaHomePage;
    private AgodaSearchResultsPage agodaSearchResultsPage;

    @BeforeMethod
    void setup() {
        checkInDate = DateTimeUtils.getNextFriday();
        checkOutDate = checkInDate.plusDays(3);

        agodaHomePage = new AgodaHomePage();
        agodaSearchResultsPage = new AgodaSearchResultsPage();

        step("Navigate to home page");
        agodaHomePage.openAgodaHomePage();
    }

    @Test(description = "Search and sort hotel successfully")
    void TC01_Search_Sort_Hotel() {

        String place = "Da Nang";
        int targetRooms = 2;
        int targetAdults = 4;
        String dateFormat = "yyyy-MM-dd";

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

