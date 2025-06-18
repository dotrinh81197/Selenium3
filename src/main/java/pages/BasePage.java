package pages;

import com.codeborne.selenide.Configuration;

public class BasePage {

    public BasePage() {

        String env = System.getProperty("Env", "agoda");
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
