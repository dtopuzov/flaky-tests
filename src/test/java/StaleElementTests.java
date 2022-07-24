import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * StaleElementReferenceException is the most popular exception in Selenium world!
 * It happens when DOM refresh, and we reference element that was found before refresh happened.
 * It can mean bug in the application under test (unwanted refresh), but sometimes it is expected.
 * <p>
 * If we need to handle it the rule is simple - just locate the element once again after refresh happened
 * - we can do it simply by calling `search = driver.findElement(By.id("searchInput"));` once again
 * - but we can also create some abstractions that will always re-locate element before interaction
 * - (just one of the examples) we may use lazy loading design pattern, please see:
 * <a href="https://www.automatetheplanet.com/lazy-loading-design-pattern/">loading design pattern</a>
 */
public class StaleElementTests {
    private WebDriver driver;

    @BeforeEach
    public void startBrowser() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--window-size=1366,768");

        driver = new ChromeDriver(chromeOptions);
        driver.navigate().to("https://www.wikipedia.org/");
    }

    @AfterEach
    public void closeBrowser() {
        driver.quit();
    }

    @Test
    public void staleElementExceptionThrownOnPageRefresh() {
        WebElement search = driver.findElement(By.id("searchInput"));
        search.sendKeys("Dimitar");

        // Simulate some dynamic element refresh on you page
        driver.navigate().refresh();

        // If we try to interact with same element it will throw StaleElementReferenceException
        StaleElementReferenceException thrown = Assertions.assertThrows(StaleElementReferenceException.class, () -> {
            search.sendKeys("Topuzov");
        });

        String expectedMessage = "stale element reference: element is not attached to the page document";
        Assertions.assertTrue(thrown.getMessage().contains(expectedMessage));
    }

    @Test
    public void staleElementExceptionThrownOnPageRefreshWorkaround1() {
        WebElement search = driver.findElement(By.id("searchInput"));
        search.sendKeys("Dimitar");

        // Simulate some dynamic element refresh on you page
        driver.navigate().refresh();

        // If we locate the element again after refresh happened then it will fail
        search = driver.findElement(By.id("searchInput"));
        search.sendKeys("Topuzov");
    }

    @Test
    public void staleElementExceptionThrownOnPageRefreshWorkaround2() {
        By searchInputLocator = By.id("searchInput");

        typeInElement(driver, searchInputLocator, "Dimitar");

        // Simulate some dynamic element refresh on you page
        driver.navigate().refresh();

        // Call method that search for element every time exactly before sendKeys
        // This should allow us not to care when DOM refresh and when not, we always first search then interact.
        typeInElement(driver, searchInputLocator, "Topuzov");
    }

    public void typeInElement(WebDriver driver, By locator, String text) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        element.sendKeys(text);
    }
}
