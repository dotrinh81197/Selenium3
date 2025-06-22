package listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {
    private static final Logger log = LoggerFactory.getLogger(RetryAnalyzer.class);

    private static final int maxRetryCount = 2;
    private int retryCount = 1;

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < maxRetryCount) {
            log.info("Retrying {} (attempt {})", result.getName(), retryCount);
            retryCount++;
            return true;
        }
        return false;
    }
}