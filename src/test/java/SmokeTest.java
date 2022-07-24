import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import static org.junit.jupiter.api.Assertions.assertEquals;

/*
Nothing useful for you here.
This is the code of example used in slides.
 */
public class SmokeTest {
    @Disabled("This test is expected to fail")
    @Test
    public void searchTest() {
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.navigate().to("https://www.wikipedia.org/");
        driver.findElement(By.id("searchInput")).sendKeys("Selenium");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        assertEquals(20, driver.findElements(By.cssSelector(".mw-search-result")).size());
        driver.quit();
    }
}
