package pages.Agoda;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import pages.BasePage;

import java.util.List;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;

public class AgodaHotelDetailPage extends BasePage {

    private final SelenideElement hotelName = $x("//h1[@data-selenium='hotel-header-name']");
    private final SelenideElement hotelAddress = $x("//span[@data-selenium='hotel-address-map']");
    private final ElementsCollection facilityTags = $$x("//div[@id='abouthotel-features']//div//li");
    private final SelenideElement reviewTooltipIcon = $x("//button[@data-testid='review-tooltip-icon']");
    private final ElementsCollection reviewLabels = $$x("//div[contains(@class,'Review-travelerGrade-Cell')]//span");


    @Step("Verify hotel name is displayed")
    public void verifyHotelNameDisplayed(String expectedHotelName) {
        hotelName.shouldBe(visible).shouldHave(text(expectedHotelName));
    }

    @Step("Verify destination is '{0}'")
    public void verifyHotelDestination(String expectedPlace) {
        hotelAddress.shouldBe(visible)
                .shouldHave(text(expectedPlace));
    }

    @Step("Verify hotel facilities include: {expectedFacilities}")
    public void verifyHotelFacilities(String... expectedFacilities) {
        for (String facility : expectedFacilities) {
            facilityTags.findBy(text(facility)).shouldBe(visible);
        }
    }

    @Step("Hover to show hotel review point detail popup")
    public void showHotelReviewDetail() {
        reviewTooltipIcon.shouldBe(visible).hover();
    }

    @Step("Verify hotel detail includes name: '{hotelName}', destination: '{place}', and facility: '{facility}'")
    public void verifyHotelDetailInformation(String hotelName, String place, String facility) {
        verifyHotelNameDisplayed(hotelName);
        verifyHotelDestination(place);
        verifyHotelFacilities(facility);
    }

    @Step("Verify review points contain: {reviewPoints}")
    public void verifyHotelReviewCategoriesDetailVisible(List<String> reviewPoints) {
        showHotelReviewDetail();

        for (String point : reviewPoints) {
            $$x("//div[contains(@class,'Review-travelerGrade-Cell')]//span")
                    .findBy(Condition.text(point))
                    .shouldBe(Condition.visible);
        }
    }

}
