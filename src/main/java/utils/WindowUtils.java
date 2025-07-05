package utils;

import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.WebDriver;

import java.util.Set;

public class WindowUtils {
    /**
     * Closes all browser windows except the main one.
     */
    public static void closeOtherWindows() {
        WebDriver driver = WebDriverRunner.getWebDriver();
        String mainWindow = driver.getWindowHandle();
        Set<String> allWindows = driver.getWindowHandles();

        for (String window : allWindows) {
            if (!window.equals(mainWindow)) {
                driver.switchTo().window(window);
                driver.close();
            }
        }

        // Switch back to main window
        driver.switchTo().window(mainWindow);
    }

    public static void switchToWindowWithTitle(String expectedTitle) {
        WebDriver driver = WebDriverRunner.getWebDriver();

        for (String handle : driver.getWindowHandles()) {
            driver.switchTo().window(handle);
            if (driver.getTitle().contains(expectedTitle)) {
                return; // switched successfully
            }
        }

        throw new RuntimeException("No window with title containing: " + expectedTitle);
    }
}
