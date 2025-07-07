package tests;

import com.codeborne.selenide.Selenide;
import data.HotelFilterData;
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

import java.util.List;
import java.util.Map;

public class AgodaSearchFilterHotelDetailTest extends AgodaBaseTest {
    private static final List<String> EXPECTED_REVIEW_POINTS = List.of(
            "Cleanliness", "Facilities", "Service", "Location", "Value for money"
    );
    private AgodaHomePage agodaHomePage;
    private AgodaSearchResultsPage resultsPage;
    private HotelSearchData searchData;
    private HotelFilterData filterData;
    private int expectedHotelsCount;
    private int hotelIndex;
    private Map.Entry<Integer, String> firstAvailableHotel;
    private List<String> reviewPoints;
    private String resultPageTitle;

    @BeforeMethod
    void testSetup() {
        searchData = TestDataFactory.searchData();
        filterData = TestDataFactory.facilityFilter();
        hotelIndex = 5;
        expectedHotelsCount = 5;
        agodaHomePage = new AgodaHomePage();
        agodaHomePage.openAgodaHomePage();
    }

    @Test(description = "Add hotel into Favourite successfully")
    void TC03_Add_Hotel_Into_Favourite_Successfully() {

        agodaHomePage.selectCurrency("Vietnamese Dong");

        resultsPage = agodaHomePage
                .searchHotel(searchData);

        resultsPage.verifySearchResultsDisplayed(expectedHotelsCount, searchData.getDestination());

        resultPageTitle = Selenide.title();
        resultsPage.filterFacilities(filterData.getFacility());
        String hotelName = resultsPage.getHotelNameByIndex(hotelIndex);
        AgodaHotelDetailPage hotelDetailPage = resultsPage.selectHotelByIndex(hotelIndex);

        hotelDetailPage.verifyHotelDetailInformation(hotelName, searchData.getDestination(), filterData.getFacility());

        Allure.step("Back to the result filter page");
        WindowUtils.switchToWindowWithTitle(resultPageTitle);

        firstAvailableHotel = resultsPage.getFirstAvailableHotel();

        reviewPoints = resultsPage.showAndGetReviewPopup(firstAvailableHotel.getKey());
        Assert.assertEquals(reviewPoints, EXPECTED_REVIEW_POINTS, "Review points mismatch");

        resultsPage.gotoDetail(firstAvailableHotel.getValue());
        hotelDetailPage.verifyHotelDetailInformation(hotelName, searchData.getDestination(), filterData.getFacility());
        hotelDetailPage.verifyHotelReviewCategoriesDetailVisible(reviewPoints);

    }
}
