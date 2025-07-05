package tests.shadowDom;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.BookPwakit.BooksPage;

public class BookSearchTest {

    BooksPage bookAppPage = new BooksPage();
    String keyword = "playwright";

    @BeforeMethod
    public void setUp() {
        SelenideLogger.addListener("AllureSelenide",
                new AllureSelenide()
                        .screenshots(true)
                        .savePageSource(true)
        );
    }

    @Test
    public void searchBooksShouldContainKeyword() {
        bookAppPage.open();
        bookAppPage.search(keyword);
        bookAppPage.verifySearchResults(keyword);

    }
}
