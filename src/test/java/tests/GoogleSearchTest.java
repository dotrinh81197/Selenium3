package tests;

import com.codeborne.selenide.AssertionMode;
import com.codeborne.selenide.Configuration;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.*;

public class GoogleSearchTest extends BaseTest{

    @BeforeClass
    public void setup() {
        Configuration.browser = "chrome";
        Configuration.baseUrl = "https://www.google.com";
        Configuration.browserSize = "1920x1080";
        Configuration.headless=true;
    }

    @Test
    public void testGoogleSearch() {
        open("/");
        $("[name='q']").setValue("Selenide").pressEnter();
        $$("h3").findBy(text("selenide.org")).shouldBe(visible);
    }
}
