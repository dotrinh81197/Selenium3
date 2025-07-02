package tests;

import com.codeborne.selenide.Selenide;
import data.HotelSearchData;
import dataFactory.TestDataFactory;
import io.qameta.allure.Allure;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.Agoda.AgodaHomePage;
import pages.Agoda.AgodaHotelDetailPage;
import pages.Agoda.AgodaSearchResultsPage;
import utils.WindowUtils;

import java.time.LocalDate;
import java.util.List;

public class AgodaSearchFilterHotelDetailTest extends AgodaBaseTest {
    private static final String[] EXPECTED_REVIEW_POINTS = {
            "Cleanliness", "Facilities", "Service", "Location", "Value for money"
    };
    private AgodaHomePage agodaHomePage;
    private String place;
    private int targetRooms;
    private int targetAdults;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int expectedHotelsCount;
    private int hotelIndex;
    private String facility;

    @BeforeMethod
    void testSetup() {
        HotelSearchData data = TestDataFactory.daNangWithPool();
        place = data.getDestination();
        targetRooms = data.getRooms();
        targetAdults = data.getAdults();
        checkInDate = data.getCheckInDate();
        checkOutDate = data.getCheckOutDate();
        facility = data.getFacility();
        expectedHotelsCount = data.getExpectedResultCount();
        hotelIndex = 5;
        agodaHomePage = new AgodaHomePage();
        agodaHomePage.openAgodaHomePage();
    }

    @Test(description = "Add hotel into Favourite successfully")
    void TC03_Add_Hotel_Into_Favourite_Successfully() {

        agodaHomePage.selectCurrency("Vietnamese Dong");

        AgodaSearchResultsPage resultsPage = agodaHomePage
                .searchHotel(place, checkInDate, checkOutDate, targetRooms, targetAdults);

        resultsPage.verifySearchResultsDisplayed(expectedHotelsCount, place);

        String resultPageTitle = Selenide.title();
        resultsPage.filterFacilities(facility);
        String hotelName = resultsPage.getHotelNameByIndex(hotelIndex);
        AgodaHotelDetailPage hotelDetailPage = resultsPage.selectHotelByIndex(hotelIndex);

        hotelDetailPage.verifyHotelDetailInformation(hotelName, place, facility);

        Allure.step("Back to the result filter page");
        WindowUtils.switchToWindowWithTitle(resultPageTitle);

        hotelName = resultsPage.getFirstAvailableHotelName().getValue();
        int index = resultsPage.getFirstAvailableHotelName().getKey();

        List<String> reviewPoint = resultsPage.showAndGetReviewPopup(index);
        Assert.assertEquals(reviewPoint, List.of(EXPECTED_REVIEW_POINTS), "Review points mismatch");

        resultsPage.gotoDetail(hotelName);
        hotelDetailPage.verifyHotelDetailInformation(hotelName, place, facility);
        hotelDetailPage.verifyReviewPoints(reviewPoint);

    }
}
