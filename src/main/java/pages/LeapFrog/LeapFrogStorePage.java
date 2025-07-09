package pages.LeapFrog;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import data.GameData;
import io.qameta.allure.Step;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class LeapFrogStorePage {
    private static final int EXCEL_HEADER_ROW_COUNT = 1;
    private final SelenideElement popup = $x("//div[@data-popup-name='geo-ip-mismatch-warning-popup']//button[text()='Close']");
    private final ElementsCollection cards = $$x("//div[@class='resultList']//div[@class='catalog-product']");
    private final SelenideElement paginationSelect = $("ul.inline-links li select");

    @Step("Open LeapFrog Store")
    public void openLeapFrogStore() {
        open("/");
        closePopup();
    }

    public int getTotalResultPage() {
        return paginationSelect.getOptions().size() - 1; // Exclude the first option, which displays information but is not a valid option
    }

    private void closePopup() {
        if (popup.is(visible)) {
            popup.click();
            popup.shouldBe(hidden); // Ensure the popup is closed
        }
    }

    public List<GameData> getGameInfoOfAllPages(int totalPages) {
        List<GameData> dataList = new ArrayList<>();
        int currentPage = 1;
        int scrapedRowIndex = EXCEL_HEADER_ROW_COUNT + 1; // Start from the first data row after the header rows

        while (currentPage <= totalPages) {

            for (SelenideElement card : cards) {
                card.scrollIntoView(true).shouldBe(visible);
                String title = card.$("p.heading ").shouldBe(visible).getText().trim();
                String age = card.$("p.ageDisplay").shouldBe(visible).getText().trim();
                SelenideElement priceElement = card.$("span.single.price.sale"); // Check for sale price first
                if (!priceElement.exists()) {
                    priceElement = card.$("span.single.price");
                }
                String priceFullText = priceElement.shouldBe(visible).getText().trim();
                String price = priceFullText
                        .replaceAll("(?i)(Sale price:|Price:)", "")
                        .replaceAll("\\s+", " ")
                        .trim();

                dataList.add(new GameData(title, age, price, scrapedRowIndex++));
            }

            SelenideElement nextButton = $x("//div[@class='paginator bottom']//a[@data-page='next']");
            if (currentPage < totalPages && nextButton.is(visible)) {
                closePopup();
                nextButton.scrollIntoView(true).click();
                currentPage++;
            } else {
                break;
            }
        }
        return dataList;
    }
}
