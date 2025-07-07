package tests.contentTesting;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import data.GameData;
import dataFactory.GameDataMapper;
import io.qameta.allure.selenide.AllureSelenide;
import listeners.TestListener;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pages.LeapFrog.LeapFrogStorePage;
import utils.DataComparator;
import utils.ExcelUtils;

import java.io.File;
import java.net.URL;
import java.util.List;

import static io.qameta.allure.Allure.step;
import static org.testng.Assert.assertNotNull;

@Listeners(TestListener.class)
public class LeapFrogContentValidationTest {
    int totalPage;
    URL resource;
    private List<GameData> expectedData;
    private LeapFrogStorePage storePage;
    private List<GameData> actualData;

    @BeforeClass
    public void setup() throws Exception {
        SelenideLogger.addListener("AllureSelenide",
                new AllureSelenide()
                        .screenshots(true)
                        .savePageSource(true)
        );

        storePage = new LeapFrogStorePage();

        resource = getClass().getClassLoader().getResource("data/leapfrog-games.xlsx");
        assertNotNull(resource, "Excel file not found in resources!");
        File file = new File(resource.toURI());
        expectedData = ExcelUtils.loadExcelData(file.getAbsolutePath(), new GameDataMapper());
        storePage.openLeapFrogStore();
    }

    @Test(description = "Validate LeapFrog store content matches reference sheet")
    public void leapFrogStoreContentTest() {
        totalPage = storePage.getTotalResultPage();
        actualData = storePage.getGameInfoOfAllPages(totalPage);
        DataComparator.compareGameData(actualData, expectedData);
    }

    @AfterMethod
    void tearDown() {
        if (WebDriverRunner.hasWebDriverStarted()) {
            Selenide.closeWebDriver();
            step("Browser closed after test.");
        }
    }
}
