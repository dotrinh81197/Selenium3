package tests.contentTesting;

import data.GameData;
import dataFactory.GameDataMapper;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.LeapFrog.LeapFrogStorePage;
import utils.DataComparator;
import utils.ExcelUtils;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.testng.Assert.assertNotNull;

public class LeapFrogContentValidationTest extends LeapFrogBaseTest {
    int totalPages;
    URL resource;
    private LeapFrogStorePage storePage;
    private List<GameData> expectedData;
    private List<GameData> actualData;

    @BeforeMethod
    public void setup() throws Exception {

        storePage = new LeapFrogStorePage();

        resource = getClass().getClassLoader().getResource("data/leapfrog-games.xlsx");
        assertNotNull(resource, "Excel file not found in resources!");
        File file = new File(resource.toURI());
        expectedData = ExcelUtils.loadExcelData(file.getAbsolutePath(), new GameDataMapper());
        storePage.openLeapFrogStore();
    }

    @Test(description = "Validate LeapFrog store content matches reference sheet")
    public void leapFrogStoreContentTest() {
        totalPages = storePage.getTotalResultPage();
        actualData = storePage.getGameInfoOfAllPages(totalPages);
        DataComparator.compareGameData(actualData, expectedData);
    }

}
