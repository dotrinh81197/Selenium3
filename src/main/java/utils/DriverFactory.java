package utils;

import com.codeborne.selenide.Configuration;

public class DriverFactory {
    public static void setupChrome() {
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";
        Configuration.timeout = 5000;
    }
}
