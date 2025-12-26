package com.testinium;

import com.testinium.driver.TestiniumSeleniumDriver;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver; // sadece import gerekirse kalsın
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver; // sadece import gerekirse kalsın
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Actions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Hooks {

    // Fallback; property/env yoksa buna düşer
    //private final String defaultHub = "http://host.docker.internal:4444/wd/hub";
    private final String defaultHub = "http://hub-devcluster.testinium.io:4444/wd/hub";

    protected static WebDriver driver;
    protected static Actions actions;

    @Before
    public void beforeTest() throws MalformedURLException {
        String browser = System.getProperty("browser", "chrome").toLowerCase();

        // URL önceliği: nodeUrl > hubURL > default
        String gridUrl = firstNonBlank(
                System.getProperty("nodeUrl"),
                System.getProperty("hubURL"),
                defaultHub
        );
        URL grid = new URL(gridUrl);

        switch (browser) {
            case "firefox" -> {
                FirefoxOptions options = firefoxOptions();
                // key vb. herhangi bir vendor capability EKLEME!
                driver = new TestiniumSeleniumDriver(grid, options);
            }
            default -> {
                ChromeOptions options = chromeOptions();
                // key vb. herhangi bir vendor capability EKLEME!
                driver = new TestiniumSeleniumDriver(grid, options);
            }
        }

        actions = new Actions(driver);
    }

    @After
    public void afterTest() {
        if (driver != null) {
            try { driver.quit(); } finally { driver = null; actions = null; }
        }
    }

    public static WebDriver getWebDriver() { return driver; }

    // -------- Options Builders --------

    public ChromeOptions chromeOptions() {
        ChromeOptions options = new ChromeOptions();

        // Bildirimleri kapat vb.
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        options.setExperimentalOption("prefs", prefs);

        options.addArguments("--disable-notifications");
        options.addArguments("--start-fullscreen");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");

        if (Boolean.parseBoolean(System.getProperty("headless", "false"))) {
            options.addArguments("--headless=new");
        }

        options.setAcceptInsecureCerts(true);
        return options;
    }

    public FirefoxOptions firefoxOptions() {
        FirefoxOptions options = new FirefoxOptions();
        options.addPreference("dom.webnotifications.enabled", false);
        options.addArguments("--kiosk");
        options.addArguments("--start-fullscreen");
        if (Boolean.parseBoolean(System.getProperty("headless", "false"))) {
            options.addArguments("-headless");
        }
        options.setAcceptInsecureCerts(true);
        return options;
    }

    // -------- helpers --------
    private static String firstNonBlank(String... vals) {
        if (vals == null) return null;
        for (String v : vals) if (v != null && !v.isBlank()) return v;
        return null;
    }
}
