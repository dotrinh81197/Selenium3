package tests;

import data.HotelFilterData;
import data.HotelSearchData;
import dataFactory.TestDataFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.Agoda.AgodaHomePage;
import pages.Agoda.AgodaSearchResultsPage;

import static io.qameta.allure.Allure.step;

public class AgodaSearchAndFilterTest extends AgodaBaseTest {

    int expectedHotelCount;
    private AgodaHomePage agodaHomePage;
    private HotelSearchData searchData;
    private HotelFilterData priceStarFilter;

    @BeforeMethod
    void setup() {
        searchData = TestDataFactory.searchData();
        priceStarFilter = TestDataFactory.priceStarFilter();
        expectedHotelCount = 5;
        agodaHomePage = new AgodaHomePage();

        step("Navigate to home page");
        agodaHomePage.openAgodaHomePage();
    }

    @Test(description = "Search and filter hotel successfully")
    void TC02_Search_Filter_Hotel() {

        agodaHomePage.selectCurrency("Vietnamese Dong");

        AgodaSearchResultsPage resultsPage = agodaHomePage
                .searchHotel(searchData);

        resultsPage.verifySearchResultsDisplayed(expectedHotelCount, searchData.getDestination());

        resultsPage.submitFilterInfo(priceStarFilter);

        resultsPage.verifyFilterHighlighted(priceStarFilter.getMinPrice(), priceStarFilter.getMaxPrice(), priceStarFilter.getRating());

        resultsPage.verifyFilterResult(searchData.getDestination(), priceStarFilter.getMinPrice(), priceStarFilter.getMaxPrice(), priceStarFilter.getRating(), expectedHotelCount);

        resultsPage.resetPriceFilter();

        resultsPage.verifyPriceSliderReset();
    }
}
