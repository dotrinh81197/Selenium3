package pages.BookPwakit;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.testng.Assert;
import pages.BasePage;

import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static utils.ShadowUtils.getNestedShadowElement;
import static utils.ShadowUtils.getShadowElement;
import static utils.ShadowUtils.getShadowElements;
import static utils.ShadowUtils.waitForShadowElement;

public class BooksPage extends BasePage {


    public void open() {
        Selenide.open("https://books-pwakit.appspot.com/");
        $("book-app").shouldBe(com.codeborne.selenide.Condition.visible);
    }

    public void search(String keyword) {
        // Traverse:
        // book-app (shadow) -> app-header (shadow)
        SelenideElement appHeader = getShadowElement(
                "book-app",
                "app-header"
        );

        // app-toolbar.toolbar-bottom (light DOM under app-header)
        SelenideElement toolbar = appHeader.find("app-toolbar.toolbar-bottom");

        // book-input-decorator (shadow)
        SelenideElement inputDecorator = toolbar.find("book-input-decorator");

        // input#input (shadow)
        SelenideElement searchInput = inputDecorator.find("input#input");

        searchInput.setValue(keyword).pressEnter();
    }


    public List<SelenideElement> getAllBookItemsResult() {
        try {
            SelenideElement bookResult = waitForShadowElement(defaultTimeout, "book-app", "book-explore", "book-item");
            bookResult.shouldBe(com.codeborne.selenide.Condition.visible, defaultTimeout);

            return getShadowElements(
                    "book-app",
                    "book-explore",
                    "book-item"
            );

        } catch (Exception e) {
            System.err.println("❌ Error while retrieving book items: " + e.getMessage());
            e.printStackTrace();
            throw new AssertionError("Failed to retrieve and stabilize <book-item> elements under <book-explore>", e);
        }
    }


    public String getBookTitle(SelenideElement bookItem) {
        // Locate the <h2 class="title"> inside the book-item's shadow DOM
        SelenideElement titleElement = getNestedShadowElement(bookItem, "a > div > div > div > h2.title");

        if (titleElement == null) {
            throw new AssertionError("Title element not found inside book item");
        }

        return titleElement.getText();
    }


    public void verifySearchResults(String keyword) {
        List<SelenideElement> books = getAllBookItemsResult();

        Assert.assertFalse(books.isEmpty(), String.format("No books found for the search term: %s", keyword));

        for (SelenideElement book : books) {
            String title = getBookTitle(book).toLowerCase();
            Assert.assertTrue(
                    title.contains(keyword.toLowerCase()),
                    String.format("Book title does not contain keyword: ", title)
            );
        }
    }
}
