package tests;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.Agoda.AgodaHomePage;
import pages.Agoda.AgodaHotelDetailPage;
import pages.Agoda.AgodaSearchResultsPage;
import utils.DateTimeUtils;

import java.time.LocalDate;
import java.util.List;

public class AgodaAddToFavouriteTest extends AgodaBaseTest {

    // ✅ Declare review points as constant in the test class
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

    @BeforeMethod
    void testSetup() {
        place = "Da Nang";
        targetRooms = 2;
        targetAdults = 4;
        checkInDate = DateTimeUtils.getNextFriday();
        checkOutDate = checkInDate.plusDays(3);
        expectedHotelsCount = 5;
        agodaHomePage = new AgodaHomePage();

        agodaHomePage.openAgodaHomePage();
    }

    @Test(description = "Add hotel into Favourite successfully")
    void TC03_Add_Hotel_Into_Favourite_Successfully() {

        agodaHomePage.selectCurrency("Vietnamese Dong");

        AgodaSearchResultsPage resultsPage = agodaHomePage
                .searchHotel(place, checkInDate, checkOutDate, targetRooms, targetAdults);

        resultsPage.verifySearchResultsDisplayed(expectedHotelsCount, place);

        resultsPage.filterFacilities("Non-smoking");
        String hotelName = resultsPage.getHotelNameByIndex(5);
        AgodaHotelDetailPage hotelDetailPage = resultsPage.selectHotelByIndex(5);

        hotelDetailPage.verifyHotelNameDisplayed(hotelName);
        hotelDetailPage.verifyHotelDestination(place);
        hotelDetailPage.verifyFacilities("Swimming pool");

        hotelDetailPage.showHotelReviewDetail();

        hotelDetailPage.verifyDetailedReviewPointsPresent(List.of(EXPECTED_REVIEW_POINTS));
    }
}
