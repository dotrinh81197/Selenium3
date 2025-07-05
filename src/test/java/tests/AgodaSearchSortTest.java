package tests;

import data.HotelSearchData;
import dataFactory.TestDataFactory;
import listeners.TestListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pages.Agoda.AgodaHomePage;
import pages.Agoda.AgodaSearchResultsPage;

import static io.qameta.allure.Allure.step;

@Listeners(TestListener.class)
public class AgodaSearchSortTest extends AgodaBaseTest {

    private AgodaHomePage agodaHomePage;
    private HotelSearchData searchData;
    private int expectedHotelsCount;

    @BeforeMethod
    void testSetup() {
        searchData = TestDataFactory.searchData();
        agodaHomePage = new AgodaHomePage();
        expectedHotelsCount = 5;
        
        step("Navigate to home page");
        agodaHomePage.openAgodaHomePage();
    }

    @Test(description = "Search and sort hotel successfully")
    void TC01_Search_Sort_Hotel() {

        agodaHomePage.selectCurrency("Vietnamese Dong");

        AgodaSearchResultsPage resultsPage = agodaHomePage
                .searchHotel(searchData);

        resultsPage.verifySearchResultsDisplayed(expectedHotelsCount, searchData.getDestination());

        resultsPage.sortByLowestPrice();

        resultsPage.verifyLowestPriceSortOrder(searchData.getDestination(), expectedHotelsCount);
    }
}

