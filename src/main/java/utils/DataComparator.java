package utils;

import data.GameData;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.testng.Assert.fail;

public class DataComparator {

    @Step("Compare UI data with reference data, row-by-row, showing mismatches, missing, and unexpected rows")
    public static void compareGameData(List<GameData> actualUiData, List<GameData> expectedExcelData) {
        List<String> mismatches = new ArrayList<>();
        List<GameData> missingInUi = new ArrayList<>();
        List<GameData> unexpectedInUi = new ArrayList<>();

        int maxSize = Math.max(expectedExcelData.size(), actualUiData.size());

        for (int i = 0; i < maxSize; i++) {
            GameData expected = i < expectedExcelData.size() ? expectedExcelData.get(i) : null;
            GameData actual = i < actualUiData.size() ? actualUiData.get(i) : null;

            if (expected == null && actual != null) {
                unexpectedInUi.add(actual);
            } else if (expected != null && actual == null) {
                missingInUi.add(expected);
            } else if (expected != null) {
                boolean mismatch =
                        !normalize(expected.title).equals(normalize(actual.title)) ||
                                !expected.age.equals(actual.age) ||
                                !expected.price.equals(actual.price);

                if (mismatch) {
                    mismatches.add(
                            "Expected:\n" + expected +
                                    "\nActual:\n" + actual + "\n"
                    );
                }
            }
        }

        if (!mismatches.isEmpty() || !missingInUi.isEmpty() || !unexpectedInUi.isEmpty()) {
            StringBuilder report = new StringBuilder();

            if (!mismatches.isEmpty()) {
                report.append("====== Mismatched Rows (Expected vs Actual) ======\n")
                        .append("Total: ").append(mismatches.size()).append("\n\n");
                mismatches.forEach(r -> report.append(r).append("---\n"));
                report.append("\n");
            }

            if (!missingInUi.isEmpty()) {
                report.append("====== Missing in UI (Present in Expected, Missing in Actual) ======\n")
                        .append("Total: ").append(missingInUi.size()).append("\n\n")
                        .append(missingInUi.stream()
                                .map(GameData::toString)
                                .collect(Collectors.joining("\n")))
                        .append("\n\n");
            }

            if (!unexpectedInUi.isEmpty()) {
                report.append("====== Unexpected in UI (Present in Actual, Missing in Expected) ======\n")
                        .append("Total: ").append(unexpectedInUi.size()).append("\n\n")
                        .append(unexpectedInUi.stream()
                                .map(GameData::toString)
                                .collect(Collectors.joining("\n")))
                        .append("\n");
            }

            attachText("LeapFrog Content Detailed Comparison Report", report.toString());
            fail("Discrepancies found between Excel data and UI data. See attachment for details.");
        }
    }

    private static String normalize(String input) {
        return input == null ? "" : input.trim().replace("\u00A0", " ").replaceAll("\\s+", " ").toLowerCase();
    }

    @Attachment(value = "{0}", type = "text/plain")
    private static String attachText(String name, String content) {
        return content;
    }
}
