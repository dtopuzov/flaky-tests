import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * Flakiness can be caused by not deterministic browser size.
 * Here we will show some examples for both good and bad practices.
 * Please read the comments in each test.
 */
public class BrowserSizeTests {
    @Test
    public void flakinessByBrowserMaximize() {
        WebDriver driver = new ChromeDriver();

        // Maximize is not deterministic, it may be different on different machines.
        // Even in headless mode and docker it is still not deterministic.
        // You can find more docker specific details here:
        // https://github.com/SeleniumHQ/docker-selenium/issues/220
        driver.manage().window().maximize();

        driver.navigate().to("https://www.wikipedia.org/");
        driver.quit();
    }

    @Test
    public void reduceFlakinessByFixingBrowserSize() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--window-size=1366,768");
        chromeOptions.addArguments("--headless");

        WebDriver driver = new ChromeDriver(chromeOptions);
        driver.navigate().to("https://www.wikipedia.org/");

        Dimension size = driver.manage().window().getSize();

        String getHeightScript = "return document.documentElement.clientHeight";
        int viewportHeight = Integer.parseInt(((JavascriptExecutor) driver).executeScript(getHeightScript).toString());

        driver.quit();

        Assertions.assertEquals(1366, size.width);
        Assertions.assertEquals(768, size.height);
        Assertions.assertEquals(768, viewportHeight);
    }

    @Test
    public void settingsSizeButNotRunningHeadlessIsDifferent() {
        // Let's have the test above, but skip headless flag.
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--window-size=1366,768");
        chromeOptions.addArguments("--force-device-scale-factor=1");

        WebDriver driver = new ChromeDriver(chromeOptions);
        driver.navigate().to("https://www.wikipedia.org/");

        Dimension size = driver.manage().window().getSize();
        String getHeightScript = "return document.documentElement.clientHeight";
        int viewportHeight = Integer.parseInt(((JavascriptExecutor) driver).executeScript(getHeightScript).toString());

        driver.quit();

        // Total height of browser window (viewport + toolbars) is 768.
        // View port is less, how much less depends on browser type and version.
        Assertions.assertEquals(768, size.height);
        Assertions.assertTrue(768 > viewportHeight);
    }

    @Test
    public void sometimesDisplayScaleMattersToo() {
        // Let's assume we fix the browser size, but are you sure where your test runs?
        // What if it runs on Apple machine with retina display?
        // Nowadays, we have more and more displays (not only Apple) using not default DPI ratio.

        // This can be resolved by forcing scale factor
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--window-size=1366,768");
        chromeOptions.addArguments("--force-device-scale-factor=1");
    }
}
