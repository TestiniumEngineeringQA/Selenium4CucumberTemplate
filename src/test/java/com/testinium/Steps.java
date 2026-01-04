package com.testinium;

import com.testinium.helper.ElementHelper;
import com.testinium.helper.StoreHelper;
import com.testinium.model.ElementInfo;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

@Slf4j
public class Steps {

    private static final int TIMEOUT_SEC = 30;
    private static final int POLL_MS = 150;

    private final WebDriver driver;
    private final Actions actions;
    private final WebDriverWait wait;

    public Steps() {
        this.driver = Hooks.getDriver();
        this.actions = new Actions(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_SEC), Duration.ofMillis(POLL_MS));
    }

    private WebElement findElement(String key) {
        ElementInfo elementInfo = StoreHelper.INSTANCE.findElementInfoByKey(key);
        if (elementInfo == null) {
            throw new IllegalArgumentException("Element key not found in repository: " + key);
        }

        By by = ElementHelper.getElementInfoToBy(elementInfo);

        WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(by));

        if (driver instanceof JavascriptExecutor) {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({behavior:'smooth', block:'center', inline:'center'})",
                    el
            );
        }
        return el;
    }

    private void hover(String key) {
        actions.moveToElement(findElement(key)).perform();
    }

    private void clickElement(String key) {
        hover(key);
        findElement(key).click();
    }

    // ---------------- Steps ----------------

    @Given("{string} sayfasina git")
    public void sayfasinaGit(String uri) {
        driver.get(uri);
        log.info("Navigating to: {}", uri);
    }

    @Then("{int} saniye bekle")
    public void saniyeBekle(int seconds) {
        waitByMilliSeconds(seconds * 1000L);
    }

    @Given("{string} elementine tikla")
    public void elementineTikla(String key) {
        if (key != null && !key.isBlank()) {
            clickElement(key);
            log.info("{} element clicked.", key);
        }
    }

    @Given("{string} elementine {string} degerini yaz")
    public void elementineDegeriniYaz(String key, String text) {
        WebElement el = findElement(key);
        el.clear();
        el.sendKeys(text);
        log.info("Typed '{}' into '{}'", text, key);
    }

    @Given("{long} milisaniye bekle")
    public void waitByMilliSeconds(long milliseconds) {
        try {
            log.info("{} ms waiting...", milliseconds);
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Wait interrupted", e);
        }
    }
}
