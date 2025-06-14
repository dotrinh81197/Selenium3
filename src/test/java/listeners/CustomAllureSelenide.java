package listeners;

import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.LogEvent;
import io.qameta.allure.Allure;
import io.qameta.allure.selenide.AllureSelenide;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriverException;

import java.io.ByteArrayInputStream;
import java.util.Objects;

public class CustomAllureSelenide extends AllureSelenide {
    @Override
    public void afterEvent(LogEvent event) {
        super.afterEvent(event);

        if (event.getStatus() == LogEvent.EventStatus.FAIL) {
            if (WebDriverRunner.hasWebDriverStarted()) {
                try {
                    byte[] screenshot = ((TakesScreenshot) WebDriverRunner.getWebDriver())
                            .getScreenshotAs(OutputType.BYTES);
                    Allure.addAttachment("Screenshot", "image/png",
                            new ByteArrayInputStream(screenshot), ".png");

                    Allure.addAttachment("Page Source", "text/html",
                            Objects.requireNonNull(WebDriverRunner.getWebDriver().getPageSource()), ".html");
                } catch (WebDriverException e) {
                    System.out.println("Could not capture attachments: " + e.getMessage());
                }
            }
        }
    }
}
