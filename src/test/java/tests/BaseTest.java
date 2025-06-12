package tests;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import listeners.TestListener;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;

import static io.qameta.allure.Allure.step;

@Listeners(TestListener.class)
public class BaseTest {

    protected final String BASE_URL = "https://www.agoda.com"; // Replace with your actual application base URL

    @BeforeClass
    void setup() {
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());
    }

    @AfterMethod
    void tearDown() {
        if (WebDriverRunner.hasWebDriverStarted()) {
            Selenide.closeWebDriver();
            step("Browser closed after test.");
        }
        SelenideLogger.removeListener("AllureSelenide");
    }
}
