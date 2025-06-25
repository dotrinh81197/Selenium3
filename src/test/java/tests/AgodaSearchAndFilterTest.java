package tests;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.Agoda.AgodaHomePage;
import pages.Agoda.AgodaSearchResultsPage;
import utils.DateTimeUtils;

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
    int expectedHotelCount = 5;
    private AgodaHomePage agodaHomePage;

    @BeforeMethod
    void setup() {
        place = "Da Nang";
        targetRooms = 2;
        targetAdults = 4;
        checkInDate = DateTimeUtils.getNextFriday();
        checkOutDate = checkInDate.plusDays(3);
        minPrice = 500000;
        maxPrice = 1000000;
        star = "3";
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

        int minDefaultPrice = resultsPage.getMinPriceFilterDefault();
        int maxDefaultPrice = resultsPage.getMaxPriceFilterDefault();

        resultsPage.submitFilterInfo(minPrice, maxPrice, star);

        resultsPage.verifyFilterHighlighted(minPrice, maxPrice, star);

        resultsPage.verifyFilterResult(place, minPrice, maxPrice, star, expectedHotelCount);

        resultsPage.resetPriceFilter(minDefaultPrice, maxDefaultPrice);

        resultsPage.verifyPriceSliderRange(minDefaultPrice, maxDefaultPrice);

    }
}
