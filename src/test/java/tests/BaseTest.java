package tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import config.TestEnvInfo;
import io.qameta.allure.selenide.AllureSelenide;
import listeners.TestListener;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;

import static io.qameta.allure.Allure.step;

@Listeners(TestListener.class)
public class BaseTest {

    @BeforeClass
    void globalSetup() {
        String env = System.getProperty("Env", "agoda").toLowerCase();
        TestEnvInfo envInfo = new TestEnvInfo(env + ".properties");
        Configuration.baseUrl = System.getProperty("BASE_URL", envInfo.getBaseURL());

        SelenideLogger.addListener("AllureSelenide",
                new AllureSelenide()
                        .screenshots(true)
                        .savePageSource(true)
        );
    }

    @AfterMethod
    void tearDown() {
        if (WebDriverRunner.hasWebDriverStarted()) {
            Selenide.closeWebDriver();
            step("Browser closed after test.");
        }
    }
}
