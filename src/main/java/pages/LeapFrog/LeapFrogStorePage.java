package pages.LeapFrog;

import com.codeborne.selenide.SelenideElement;
import data.GameData;
import io.qameta.allure.Step;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.open;

public class LeapFrogStorePage {
    private static final String BASE_URL = "https://store.leapfrog.com/en-us/apps/c?p=%d&platforms=197&product_list_dir=asc&product_list_order=name";
    private final SelenideElement popup = $x("//div[@data-popup-name='geo-ip-mismatch-warning-popup']//button[text()='Close']");
    private final SelenideElement paginationSelect = $("ul.inline-links li select");

    private static List<GameData> fetchAndParsePage(int page) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        String url = String.format(BASE_URL, page);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        String html = client.send(request, HttpResponse.BodyHandlers.ofString()).body();

        List<GameData> dataList = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements cards = doc.select("div.resultList div.catalog-product");
        for (Element card : cards) {
            String title = card.selectFirst("p.heading").text().trim();
            String age = card.selectFirst("p.ageDisplay").text().trim();
            Element priceEl = card.selectFirst("span.single.price.sale");
            if (priceEl == null) priceEl = card.selectFirst("span.single.price");
            String price = priceEl.text().replaceAll("(?i)(Sale price:|Price:)", "").trim();
            dataList.add(new GameData(title, age, price));
        }
        return dataList;
    }

    @Step("Get game info from all pages")
    public List<GameData> fetchAllGameData(int totalPage, int threadCount) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<List<GameData>>> futures = new ArrayList<>();
        for (int i = 1; i <= totalPage; i++) {
            final int page = i;
            futures.add(executor.submit(() -> fetchAndParsePage(page)));
        }
        List<GameData> allData = new ArrayList<>();
        for (Future<List<GameData>> f : futures) {
            allData.addAll(f.get());
        }
        executor.shutdown();
        return allData;
    }

    @Step("Open LeapFrog Store")
    public void openLeapFrogStore() {
        open(String.format(BASE_URL, 1));
        closePopup();
    }

    public int getTotalResultPage() {
        return paginationSelect.getOptions().size() - 1; // Exclude the first option as it is only for displaying page numbers
    }

    private void closePopup() {
        if (popup.is(visible)) {
            popup.click();
            popup.shouldBe(hidden); // Ensure the popup is closed
        }
    }

}
