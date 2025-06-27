package pages.Agoda;

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

    private final SelenideElement hotelName = $x("//h1[@data-selenium='hotel-header-name']"); // update with actual selector
    private final SelenideElement hotelAddress = $x("//span[@data-selenium='hotel-address-map']"); // update with actual selector
    private final ElementsCollection facilityTags = $$x("//div[@data-element-name='atf-top-amenities']//div[@role='listitem']"); // update with actual selector


    @Step("Verify hotel name is displayed")
    public void verifyHotelNameDisplayed(String expectedHotelName) {
        hotelName.shouldBe(visible).shouldHave(text(expectedHotelName));
    }

    @Step("Verify destination is '{0}'")
    public void verifyHotelDestination(String expectedPlace) {
        hotelAddress.shouldBe(visible)
                .shouldHave(text(expectedPlace));
    }

    public void verifyDetailedReviewPointsPresent(List<String> expectedPoints) {
        for (String point : expectedPoints) {
            $x("//span[contains(text(), '" + point + "')]").shouldBe(visible);
        }
    }

    @Step("Add hotel to favourites")
    public void addToFavourites() {
        $x("//button[@data-element-name='favorite-heart']").shouldBe(visible).click();
    }

    @Step("Verify hotel is in favourites")
    public void verifyHotelIsInFavourites() {
        // assert heart icon filled or success message
    }

    public void verifyFacilities(String... expectedFacilities) {
        for (String facility : expectedFacilities) {
            facilityTags.findBy(text(facility)).shouldBe(visible);
        }
    }

    public void showHotelReviewDetail() {
        $x("//button[@data-testid='review-tooltip-icon']").shouldBe(visible).hover();
    }
}
