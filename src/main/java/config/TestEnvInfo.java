package config;


import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Getter
public class TestEnvInfo {
    private final Properties props;

    public TestEnvInfo(String resourceName) {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            if (input == null) {
                throw new RuntimeException("Properties file not found on classpath: " + resourceName);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error loading properties file: " + resourceName, e);
        }
        this.props = properties;
    }

    public String getPropValue(String key) {
        return getPropValue(key, false);
    }

    public String getPropValue(String key, boolean isOptional) {
        Object value = props.get(key);
        if (value == null) {
            if (!isOptional) {
                throw new IllegalStateException(key + " is not set");
            }
            return "";
        }
        return value.toString();
    }

    public String getBaseURL() {
        return getPropValue("BASE_URL");
    }
}
