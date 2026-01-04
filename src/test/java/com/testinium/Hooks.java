package com.testinium;

import com.testinium.driver.TestiniumSeleniumDriver;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Actions;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Hooks {

    private static final String DEFAULT_HUB = "http://host.docker.internal:4444/wd/hub";

    private static WebDriver driver;
    private static Actions actions;

    @Before
    public void beforeScenario() throws Exception {
        String browser = System.getProperty("browser", "chrome").toLowerCase();

        String gridUrl = firstNonBlank(
                System.getProperty("nodeUrl"),
                System.getProperty("hubURL"),
                System.getenv("SELENIUM_REMOTE_URL"),
                DEFAULT_HUB
        );

        URL grid = new URL(gridUrl);

        if ("firefox".equals(browser)) {
            driver = new TestiniumSeleniumDriver(grid, firefoxOptions());
        } else {
            driver = new TestiniumSeleniumDriver(grid, chromeOptions());
        }

        actions = new Actions(driver);
    }

    @After
    public void afterScenario() {
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                driver = null;
                actions = null;
            }
        }
    }

    public static WebDriver getWebDriver() {
        if (driver == null) {
            throw new IllegalStateException("WebDriver is not initialized. Did the @Before hook run?");
        }
        return driver;
    }

    public static Actions getActions() {
        if (actions == null) {
            throw new IllegalStateException("Actions is not initialized. Did the @Before hook run?");
        }
        return actions;
    }

    // -------- Options --------

    private static ChromeOptions chromeOptions() {
        ChromeOptions options = new ChromeOptions();

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        options.setExperimentalOption("prefs", prefs);

        options.addArguments("--disable-notifications");

        if (Boolean.parseBoolean(System.getProperty("headless", "false"))) {
            options.addArguments("--headless=new");
        }

        options.setAcceptInsecureCerts(true);
        return options;
    }

    private static FirefoxOptions firefoxOptions() {
        FirefoxOptions options = new FirefoxOptions();
        options.addPreference("dom.webnotifications.enabled", false);

        if (Boolean.parseBoolean(System.getProperty("headless", "false"))) {
            options.addArguments("-headless");
        }

        options.setAcceptInsecureCerts(true);
        return options;
    }

    // -------- Helper --------

    private static String firstNonBlank(String... vals) {
        if (vals == null) return null;
        for (String v : vals) {
            if (v != null && !v.isBlank()) return v;
        }
        return null;
    }
}
