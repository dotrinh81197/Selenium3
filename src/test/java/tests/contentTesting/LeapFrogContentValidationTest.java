package tests.contentTesting;

import data.GameData;
import dataFactory.GameDataMapper;
import listeners.TestListener;
import lombok.SneakyThrows;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pages.LeapFrog.LeapFrogStorePage;
import utils.DataComparator;
import utils.ExcelUtils;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.testng.Assert.assertNotNull;

@Listeners(TestListener.class)
public class LeapFrogContentValidationTest {
    private static final String EXCEL_PATH = "data/leapfrog-games.xlsx";
    private static final int THREAD_COUNT = 10;
    int totalPage;
    private List<GameData> expectedData;
    private LeapFrogStorePage storePage;
    private List<GameData> actualData;

    @BeforeMethod
    public void setup() throws Exception {

        storePage = new LeapFrogStorePage();

        URL resource = getClass().getClassLoader().getResource(EXCEL_PATH);
        assertNotNull(resource, "Excel file not found in resources!");
        expectedData = ExcelUtils.loadExcelData(new File(resource.toURI()).getAbsolutePath(), new GameDataMapper());
        storePage.openLeapFrogStore();
        totalPage = storePage.getTotalResultPage();
    }

    @SneakyThrows
    @Test(description = "Validate LeapFrog store content matches reference sheet")
    public void leapFrogStoreContentTest() {
        actualData = storePage.fetchAllGameData(totalPage, THREAD_COUNT);
        DataComparator.compareAndReport(actualData, expectedData);
    }
}
