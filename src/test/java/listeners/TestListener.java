package listeners;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.FileSystemResultsWriter;
import io.qameta.allure.selenide.AllureSelenide;
import org.testng.IAnnotationTransformer;
import org.testng.IExecutionListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestListener implements ITestListener, IExecutionListener, IAnnotationTransformer {

    @Override
    public void onExecutionStart() {
        AllureLifecycle lifecycle = Allure.getLifecycle();

        // Get current time and format it
        String currentTime = new SimpleDateFormat("MM-dd-HH-mm-ss").format(new Date());

        FileSystemResultsWriter writer =
                new FileSystemResultsWriter(Paths.get("allure-results/" + "report-" + currentTime));
        Class<?> clazz = lifecycle.getClass();
        try {
            Field field = clazz.getDeclaredField("writer");
            field.setAccessible(true);
            field.set(lifecycle, writer);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        SelenideLogger.addListener("AllureSelenide", new AllureSelenide(lifecycle));
    }

    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }

    @Override
    public void onTestStart(ITestResult result) {
        Allure.step("▶️ Start test: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        Allure.step("❌ Failed test: " + result.getMethod().getMethodName());
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide()
                .screenshots(true)
                .savePageSource(true));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        Allure.step("✅ Passed test: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        Allure.step("⚠️ Skipped test: " + result.getMethod().getMethodName());
    }

    @Override
    public void onFinish(ITestContext context) {
        Allure.step("📋 Test suite finished: " + context.getName());
    }
}

