package tests;

import data.HotelSearchData;
import dataFactory.TestDataFactory;
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
        HotelSearchData data = TestDataFactory.daNangWithPool();
        place = data.getDestination();
        targetRooms = data.getRooms();
        targetAdults = data.getAdults();
        checkInDate = data.getCheckInDate();
        checkOutDate = data.getCheckOutDate();
        expectedHotelsCount = data.getExpectedResultCount();
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

