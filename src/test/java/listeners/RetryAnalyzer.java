package listeners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {
    private static final int maxRetryCount = 2;
    private int retryCount = 1;

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < maxRetryCount) {
            System.out.println("Retrying " + result.getName() + " (attempt " + (retryCount) + ")");
            retryCount++;
            return true;
        }
        return false;
    }
}