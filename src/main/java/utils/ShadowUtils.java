package utils;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.executeJavaScript;

public class ShadowUtils {


    /**
     * Traverse nested shadow DOM using a list of CSS selectors and return a SelenideElement.
     * It enters shadowRoot only if further selectors remain to avoid returning a ShadowRoot object.
     *
     * @param selectors CSS selectors representing each level of shadow/light DOM
     * @return SelenideElement found, or null if not found
     */
    public static SelenideElement getShadowElement(String... selectors) {
        StringBuilder script = new StringBuilder("var el = document;\n");
        for (int i = 0; i < selectors.length; i++) {
            script.append("el = el.querySelector('").append(selectors[i]).append("');\n")
                    .append("if (!el) return null;\n");
            if (i < selectors.length - 1) { // only enter shadowRoot if more selectors remain
                script.append("if (el.shadowRoot) el = el.shadowRoot;\n");
            }
        }
        script.append("return el;");

        WebElement element = executeJavaScript(script.toString());
        return element != null ? $(element) : null;
    }

    /**
     * Traverse nested shadow DOM using a list of CSS selectors and return a List of SelenideElements.
     * Useful for grabbing repeated elements inside shadow DOM.
     *
     * @param selectors CSS selectors, where the last selector should match multiple elements
     * @return List of SelenideElements, or empty if none found
     */
    public static List<SelenideElement> getShadowElements(String... selectors) {
        StringBuilder script = new StringBuilder("var el = document;\n");
        for (int i = 0; i < selectors.length - 1; i++) {
            script.append("el = el.querySelector('").append(selectors[i]).append("');\n")
                    .append("if (!el) return null;\n")
                    .append("if (el.shadowRoot) el = el.shadowRoot;\n");
        }
        script.append("return el.querySelectorAll('").append(selectors[selectors.length - 1]).append("');");

        List<WebElement> elements = executeJavaScript(script.toString());

        return elements != null
                ? elements.stream().map(e -> $(e)).collect(Collectors.toList())
                : List.of();
    }

    /**
     * Enter the shadowRoot of a given SelenideElement and find the child element using a CSS selector.
     *
     * @param parent   Parent SelenideElement having a shadow root
     * @param selector CSS selector to find inside the shadow root
     * @return SelenideElement found inside the shadow root, or null if not found
     */
    public static SelenideElement getNestedShadowElement(SelenideElement parent, String selector) {
        String script = "return arguments[0].shadowRoot ? arguments[0].shadowRoot.querySelector('" + selector + "') : null;";
        WebElement element = executeJavaScript(script, parent);
        return element != null ? $(element) : null;
    }

    public static SelenideElement waitForShadowElement(Duration timeout, String... selectors) {
        long endTime = System.currentTimeMillis() + timeout.toMillis();
        while (System.currentTimeMillis() < endTime) {
            try {
                WebElement el = getShadowElementRaw(selectors);
                if (el != null) {
                    return $(el); // trả về SelenideElement để caller tự .shouldBe(visible)
                }
            } catch (Exception ignored) {
            }
            Selenide.sleep(300); // tránh CPU cao
        }
        throw new RuntimeException("❌ Timeout: Element not found for selectors " + Arrays.toString(selectors));
    }

    public static List<SelenideElement> waitForShadowElements(Duration timeout, int minCount, String... selectors) {
        long endTime = System.currentTimeMillis() + timeout.toMillis();
        while (System.currentTimeMillis() < endTime) {
            try {
                List<SelenideElement> elements = getShadowElements(selectors);
                if (elements != null && elements.size() >= minCount) {
                    return elements;
                }
            } catch (Exception ignored) {
                // Continue polling silently
            }
            Selenide.sleep(300); // Avoid high CPU usage while polling
        }
        throw new RuntimeException("❌ Timeout: Expected at least " + minCount +
                " elements for selectors " + Arrays.toString(selectors));
    }


    public static WebElement getShadowElementRaw(String... selectors) {
        StringBuilder script = new StringBuilder("var el = document;\n");
        for (String selector : selectors) {
            script.append("el = el.querySelector('").append(selector).append("');\n")
                    .append("if (!el) return null;\n")
                    .append("if (el.shadowRoot) el = el.shadowRoot;\n");
        }
        script.append("return el;");
        return executeJavaScript(script.toString());
    }

}
