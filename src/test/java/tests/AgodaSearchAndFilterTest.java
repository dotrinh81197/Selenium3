package tests;

import data.HotelSearchData;
import dataFactory.TestDataFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.Agoda.AgodaHomePage;
import pages.Agoda.AgodaSearchResultsPage;

import java.time.LocalDate;

import static io.qameta.allure.Allure.step;

public class AgodaSearchAndFilterTest extends AgodaBaseTest {

    String place;
    int targetRooms;
    int targetAdults;
    LocalDate checkInDate;
    LocalDate checkOutDate;
    int minPrice;
    int maxPrice;
    String star;
    int expectedHotelCount;
    private AgodaHomePage agodaHomePage;

    @BeforeMethod
    void setup() {
        HotelSearchData data = TestDataFactory.daNangWithPool();
        place = data.getDestination();
        targetRooms = data.getRooms();
        targetAdults = data.getAdults();
        checkInDate = data.getCheckInDate();
        checkOutDate = data.getCheckOutDate();
        minPrice = data.getMinPrice();
        maxPrice = data.getMaxPrice();
        star = data.getStarRating();
        expectedHotelCount = data.getExpectedResultCount();
        agodaHomePage = new AgodaHomePage();

        step("Navigate to home page");
        agodaHomePage.openAgodaHomePage();
    }

    @Test(description = "Search and filter hotel successfully")
    void TC02_Search_Filter_Hotel() {

        agodaHomePage.selectCurrency("Vietnamese Dong");

        AgodaSearchResultsPage resultsPage = agodaHomePage
                .searchHotel(place, checkInDate, checkOutDate, targetRooms, targetAdults);

        resultsPage.verifySearchResultsDisplayed(expectedHotelCount, place);

        resultsPage.submitFilterInfo(minPrice, maxPrice, star);

        resultsPage.verifyFilterHighlighted(minPrice, maxPrice, star);

        resultsPage.verifyFilterResult(place, minPrice, maxPrice, star, expectedHotelCount);

        resultsPage.resetPriceFilter();

        resultsPage.verifyPriceSliderReset();
    }
}
