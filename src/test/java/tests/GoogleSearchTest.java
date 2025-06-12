package tests;

import listeners.TestListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.*;

@Listeners(TestListener.class)
public class GoogleSearchTest extends BaseTest {

    @Test(retryAnalyzer = listeners.RetryAnalyzer.class)
    public void testGoogleSearch() {
        open("/");
        $("[name='q']").setValue("Selenide").pressEnter();
        $$("h3").findBy(text("selenide.org")).shouldBe(visible);
    }
}
