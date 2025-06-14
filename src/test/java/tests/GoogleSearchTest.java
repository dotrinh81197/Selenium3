package tests;

import listeners.TestListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;

@Listeners(TestListener.class)
public class GoogleSearchTest extends BaseTest {

    @Test(description = "Google search test", retryAnalyzer = listeners.RetryAnalyzer.class)
    public void testGoogleSearch() {
        open("/");
        $("[name='q']").setValue("Selenide").pressEnter();
        $$("h3").findBy(text("selenide.org")).shouldBe(visible);
    }
}
