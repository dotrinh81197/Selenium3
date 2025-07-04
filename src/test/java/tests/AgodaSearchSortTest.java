package tests;

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

    private AgodaHomePage agodaHomePage;

    private String place;
    private int targetRooms;
    private int targetAdults;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int expectedHotelsCount = 5;

    @BeforeMethod
    void testSetup() {
        place = "Da Nang";
        targetRooms = 2;
        targetAdults = 4;
        checkInDate = DateTimeUtils.getNextFriday();
        checkOutDate = checkInDate.plusDays(3);
        expectedHotelsCount = 5;
        agodaHomePage = new AgodaHomePage();

        step("Navigate to home page");
        agodaHomePage.openAgodaHomePage();
    }

    @Test(description = "Search and sort hotel successfully")
    void TC01_Search_Sort_Hotel() {

        agodaHomePage.selectCurrency("Vietnamese Dong");

        AgodaSearchResultsPage resultsPage = agodaHomePage
                .searchHotel(place, checkInDate, checkOutDate, targetRooms, targetAdults);

        resultsPage.verifySearchResultsDisplayed(expectedHotelsCount, place);

        resultsPage.sortByLowestPrice();

        resultsPage.verifyLowestPriceSortOrder(place, expectedHotelsCount);
    }
}

