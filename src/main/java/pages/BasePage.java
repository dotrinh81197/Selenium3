package pages;

import com.codeborne.selenide.Configuration;

import java.time.Duration;

public class BasePage {
    protected final Duration defaultTimeout = Duration.ofMillis(Configuration.timeout);
}
