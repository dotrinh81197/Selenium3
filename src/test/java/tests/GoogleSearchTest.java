package tests;

import io.qameta.allure.Allure;
import listeners.TestListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Listeners(TestListener.class)
public class GoogleSearchTest extends AgodaBaseTest {

    @Test(description = "Google search test", retryAnalyzer = listeners.RetryAnalyzer.class)
    public void testGoogleSearch() throws IOException {
//        open("/");
//        $("[name='q']").setValue("Selenide").pressEnter();
//        $$("h3").findBy(text("selenide.org")).shouldBe(visible);

        File file = new File("build/test-screenshot.png");
        // Dummy screenshot (or use an actual one)
        Files.copy(Paths.get("src/test/resources/sample.png"), file.toPath(), StandardCopyOption.REPLACE_EXISTING);

        Allure.addAttachment("My Screenshot", "image/png", new FileInputStream(file), ".png");
    }
}

