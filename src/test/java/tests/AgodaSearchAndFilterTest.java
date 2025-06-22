package tests;

import com.codeborne.selenide.Selenide;
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
    String minPrice;
    String maxPrice;
    String star;
    int expectedHotelCount = 5;
    private AgodaHomePage agodaHomePage;
    private AgodaSearchResultsPage agodaSearchResultsPage;

    @BeforeMethod
    void setup() {
        place = "Da Nang";
        targetRooms = 2;
        targetAdults = 4;
        checkInDate = DateTimeUtils.getNextFriday();
        checkOutDate = checkInDate.plusDays(3);
        minPrice = "500000";
        maxPrice = "1000000";
        star = "3";
        agodaHomePage = new AgodaHomePage();
        agodaSearchResultsPage = new AgodaSearchResultsPage();

        step("Navigate to home page");
        agodaHomePage.openAgodaHomePage();
    }

    @Test(description = "Search and filter hotel successfully")
    void TC02_Search_Filter_Hotel() {

        agodaHomePage.enterDestination(place).selectDates(checkInDate, checkOutDate).setFamilyTravelers(targetRooms, targetAdults);

        agodaSearchResultsPage = agodaHomePage.clickSearchButton();

        Selenide.switchTo().window(1);

        agodaSearchResultsPage.verifySearchResultsDisplayed(5, place);

        agodaSearchResultsPage.submitFilterInfo(minPrice, maxPrice, star);

        agodaSearchResultsPage.verifyFilterHighlighted(minPrice, maxPrice, star);

        agodaSearchResultsPage.verifyFilterResult(place, minPrice, maxPrice, star, expectedHotelCount);

    }
}
