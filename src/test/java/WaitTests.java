import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.util.concurrent.Callable;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

/**
 * Selenium does not wait until element is available when you call 'findElement'.
 * This is the most common reason for flaky tests!
 * <p>
 * In order to have stable test you should take care to wait for element to be present
 * or wait for some condition to be met.
 * This class demonstrate different approaches to wait written as test methods.
 * <p>
 * Useful resources on this topic is following video:
 * <a href="https://www.youtube.com/watch?v=cgErlkHg_cI">How To Handle Waits In Selenium</a>
 */
public class WaitTests {
    private WebDriver driver;

    @BeforeEach
    public void startBrowser() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--window-size=1366,768");
        driver = new ChromeDriver(chromeOptions);
    }

    @AfterEach
    public void closeBrowser() {
        driver.quit();
    }

    @Test
    public void neverUseThreadSleep() {
    }

    @Test
    public void implicitWaitIsBad() {
    }

    @Test
    public void betterUseExplicitWait() {
    }

    @Test
    public void fluentWaitIsGoodButNotAvailableForAllLanguages() {
    }

    @Test
    public void customWaitUtilsCanHelpForEverythingNoMaterIfItIsSeleniumOrNot(){
        // driver.findElement(By.id("export")).click();

        // This will wait until "isFileDownloaded" return true, up to 10 seconds.
        // await().atMost(10, SECONDS).until(isFileDownloaded());
    }

    private Callable<Boolean> isFileDownloaded() {
        File file = new File("");
        return () -> file.exists();
    }
}
