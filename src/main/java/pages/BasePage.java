package pages;

import com.codeborne.selenide.Configuration;

public class BasePage {

    public BasePage() {
        Configuration.browser = System.getProperty("Browser", "chrome");
        Configuration.headless = Boolean.parseBoolean(System.getProperty("Headless", "false"));
        Configuration.pageLoadTimeout = Long.parseLong(System.getProperty("PageLoadTimeout", "50000"));
        Configuration.browserSize = System.getProperty("BrowserSize", "1366x768");
        Configuration.pageLoadStrategy = System.getProperty("PageLoadStrategy", "normal");

        String env = System.getProperty("env", "agoda"); // default to agoda
        switch (env.toLowerCase()) {
            case "agoda":
                Configuration.baseUrl = "https://www.agoda.com";
                break;
            case "vj":
                Configuration.baseUrl = "https://www.vietjetair.com";
                break;
            default:
                throw new RuntimeException("Unknown env: " + env);
        }
    }

}
