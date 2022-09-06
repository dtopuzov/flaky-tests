import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Example how to wait until element stop moving.
 */
public class MovingElementTests {
    private WebDriver driver;

    @BeforeEach
    public void startBrowser() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--window-size=1366,768");
        driver = new ChromeDriver(chromeOptions);

        ClassLoader loader = MovingElementTests.class.getClassLoader();
        String waitDemoPage = loader.getResource("moving.html").getPath();

        driver.navigate().to(String.format("file://%s", waitDemoPage));
    }

    @AfterEach
    public void closeBrowser() {
        driver.quit();
    }

    @Test
    public void waitForAnimationToFinish() {
        driver.findElement(By.id("play")).click();

        var wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until((driver) -> {
            var box = driver.findElement(By.cssSelector(".box"));
            var rectangle = box.getRect();
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
            return box.getRect().equals(rectangle);
        });
    }
}
