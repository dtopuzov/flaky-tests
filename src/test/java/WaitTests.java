import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
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

        ClassLoader loader = WaitTests.class.getClassLoader();
        String waitDemoPage = loader.getResource("wait.html").getPath();

        driver.navigate().to(String.format("file://%s", waitDemoPage));
    }

    @AfterEach
    public void closeBrowser() {
        driver.quit();
    }

    @Test
    public void neverUseThreadSleep() throws InterruptedException {
        Thread.sleep(3000);
        // What if element is displayed after 6000ms -> throw
        // What if element is displayed after 1000ms -> your test will be 2000ms slower than needed.
        WebElement button = driver.findElement(By.cssSelector("#div p"));
        Assertions.assertTrue(button.isDisplayed());
    }

    @Test
    public void implicitWaitIsBad() {
        // implicitlyWait will solve the problem
        // Initially it looks easy -> one line of code and it will always wait.
        //
        // Later you will encounter a lot of disadvantages:
        // - It is global for the session
        // - What if sometimes you need 10 seconds, then you need 30, then only 3?
        // - What if you want to check that element is not available?
        // - What if you need more flexible wait (not only to be located, but to be visible)?
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        WebElement paragraph = driver.findElement(By.cssSelector("#div p"));
        Assertions.assertTrue(paragraph.isDisplayed());
    }

    @Test
    public void implicitWaitIsVeryBadSometimes() {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        WebElement paragraph = driver.findElement(By.id("button"));

        // "button" element is available in dom immediately
        // so line above will not wait 3 seconds until it is displayed.
        // Guess what will happen if you assert it is visible?
        // It is not going to be visible and next assert will fail.
        // Assertions.assertTrue(paragraph.isDisplayed());

        Assertions.assertFalse(paragraph.isDisplayed());
    }

    @Test
    public void betterUseExplicitWait() {
        // Paragraph element is not available in DOM initially, but will be added after 3 seconds.
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement paragraph = wait.until(ExpectedConditions.presenceOfElementLocated((By.cssSelector("#div p"))));
        Assertions.assertTrue(paragraph.isDisplayed());
    }

    @Test
    public void explicitWaitWillAlsoWorkIfElementIsInDomAndYouWantToWaitUntilItChangeStateToBeVisible() {
        // Input with ID=button is available in DOM since the beginning, but will appear after 3 seconds.
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // presenceOfElementLocated will immediately return the element, but will not wait to be visible
        // Good news is that ExpectedConditions API is rich and
        // "visibilityOfElementLocated" will wait not only to be available in dom, but also to be displayed
        WebElement paragraph = wait.until(ExpectedConditions.visibilityOfElementLocated((By.id("button"))));
        Assertions.assertTrue(paragraph.isDisplayed());
    }

    @Test
    public void waitForClickable() {
        // Initially element is displayed, but disabled.
        WebElement disabledInput = driver.findElement(By.id("disabled"));
        Assertions.assertTrue(disabledInput.isDisplayed());
        Assertions.assertFalse(disabledInput.isEnabled());

         // Wait until it is clickable (enabled)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(disabledInput));
        Assertions.assertTrue(disabledInput.isEnabled());
    }

    @Test
    public void fluentWaitIsGoodButNotAvailableForAllLanguages() {
        // FluentWait is even more flexible than ExplicitWait.
        // It allows setting polling interval and is able to ignore exceptions.
        // It is available in Selenium's Java bindings, but not available in some other bindings.
        Wait<WebDriver> fluentWait = new FluentWait<WebDriver>(driver)
                .withTimeout(Duration.ofSeconds(10))
                .pollingEvery(Duration.ofMillis(100))
                .ignoring(NoSuchElementException.class);

        WebElement paragraph = fluentWait.until(ExpectedConditions.visibilityOfElementLocated((By.id("button"))));
        Assertions.assertTrue(paragraph.isDisplayed());
    }

    @Test
    public void customWaitUtilsCanHelpForEverythingNoMaterIfItIsSeleniumOrNot() {
        // ExpectedConditions API is rich, it also supports defining custom expected conditions.
        // But in some cases you may end up waiting for something that is not browser related at all.
        // For example imagine you click on button that export page as PDF file and you need to assert file is saved.
        // With https://github.com/awaitility/awaitility you can wait until some Boolean function return true.
        //

        // Imagine you click some button that will cause file to be downloaded
        // Ypu have method that check if file exisist and you want to rerun it until file exists returns true.
        await().atMost(10, SECONDS).until(isFileDownloaded());
    }

    private Callable<Boolean> isFileDownloaded() {
        // Well, in this case it is some other file, no real example where we export file from page.
        // But in general -> we can write anything that returns Boolean.
        ClassLoader loader = WaitTests.class.getClassLoader();
        File file = new File(loader.getResource("wait.html").getPath());
        return () -> file.exists();
    }
}
