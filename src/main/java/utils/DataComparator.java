package utils;

import data.GameData;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.fail;

public class DataComparator {

    @Step("Compare expected Excel data with actual UI data, showing mismatches and missing titles")
    public static void compareAndReport(List<GameData> actual, List<GameData> expected) {
        List<String> missingTitles = new ArrayList<>();
        List<String> mismatchedFields = new ArrayList<>();

        for (GameData exp : expected) {
            GameData act = actual.stream()
                    .filter(a -> a.title.equals(exp.title))
                    .findFirst()
                    .orElse(null);

            if (act == null) {
                missingTitles.add(exp.title);
            } else {
                List<String> mismatches = new ArrayList<>();
                if (!act.age.equals(exp.age)) {
                    mismatches.add("Age (expected: " + exp.age + ", actual: " + act.age + ")");
                }
                if (!act.price.equals(exp.price)) {
                    mismatches.add("Price (expected: " + exp.price + ", actual: " + act.price + ")");
                }
                if (!mismatches.isEmpty()) {
                    mismatchedFields.add(exp.title + ": " + String.join(", ", mismatches));
                }
            }
        }

        StringBuilder report = new StringBuilder();
        if (!missingTitles.isEmpty()) {
            report.append("Missing titles (").append(missingTitles.size()).append("):\n");
            missingTitles.forEach(t -> report.append(" - ").append(t).append("\n"));
        }
        if (!mismatchedFields.isEmpty()) {
            report.append("Mismatched fields (").append(mismatchedFields.size()).append("):\n");
            mismatchedFields.forEach(info -> report.append(" - ").append(info).append("\n"));
        }

        if (!report.isEmpty()) {
            attachText("LeapFrog Excel vs UI Comparison Report", report.toString());
            fail("Discrepancies found between Excel and UI data. See Allure attachment for details.");
        }
    }

    @Attachment(value = "{0}", type = "text/plain")
    private static String attachText(String name, String content) {
        return content;
    }
}
