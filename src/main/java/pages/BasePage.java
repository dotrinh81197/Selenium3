package pages;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;

public class BasePage {

    String BASE_URL = "https://www.agoda.com";

    public BasePage() {

        Configuration.browser = "chrome";
        Configuration.baseUrl = BASE_URL;
        Configuration.browserSize = "1920x1080";
        Configuration.headless = false;
        Configuration.pageLoadTimeout = 50000;
    }

    public <T> T openPath(String path, Class<T> pageObjectClass) {
        open(path);
        // Selenide's 'page' method creates an instance of the given Page Object class.
        // It's useful here to ensure the correct page object type is returned for chaining.
        return com.codeborne.selenide.Selenide.page(pageObjectClass);
    }

    public void waitForPageLoad() {
        WebDriver driver = WebDriverRunner.getWebDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Wait for document ready
        wait.until(webDriver ->
                ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete")
        );

        // Wait for jQuery AJAX (if jQuery is present)
        wait.until(webDriver -> {
            try {
                return (Long) ((JavascriptExecutor) webDriver)
                        .executeScript("return window.jQuery != null && jQuery.active === 0");
            } catch (Exception e) {
                // jQuery not present – skip wait
                return true;
            }
        });
    }

    /**
     * Closes the WebDriver instance.
     * This method can be called in test teardown.
     */
    public void tearDownBrowser() {
        closeWebDriver();
    }

}
