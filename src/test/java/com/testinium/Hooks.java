package com.testinium;

import com.testinium.driver.TestiniumSeleniumDriver;
import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Hooks {

    private static final String DEFAULT_HUB = "http://host.docker.internal:4444/wd/hub";

    private static WebDriver driver;
    private static Actions actions;

    @BeforeAll
    public static void beforeAll() throws Exception {
        String gridUrl = System.getProperty("hubURL", DEFAULT_HUB);
        String testiniumKey = System.getenv("TESTINIUM_KEY");

        ChromeOptions options = new ChromeOptions();

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        options.setExperimentalOption("prefs", prefs);

        options.addArguments("--start-fullscreen");
        options.setAcceptInsecureCerts(true);

        if (testiniumKey != null && !testiniumKey.isBlank()) {
            options.setCapability("testinium:key", testiniumKey);
        }

        driver = new TestiniumSeleniumDriver(new URL(gridUrl), options);
        actions = new Actions(driver);
    }

    @AfterAll
    public static void afterAll() {
        if (driver != null) {
            driver.quit();
            driver = null;
            actions = null;
        }
    }

    public static WebDriver getDriver() {
        return driver;
    }

    public static Actions getActions() {
        return actions;
    }
}
