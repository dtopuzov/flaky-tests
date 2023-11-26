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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

public class DownloadFile {
    private WebDriver driver;
    private final String projectFolder = System.getProperty("user.dir");
    private final Path downloadedFilePath = Path.of(projectFolder, "Products.xlsx");

    @BeforeEach
    public void startBrowser() throws IOException {
        Files.deleteIfExists(downloadedFilePath);

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--window-size=1366,768");

        Map<String, Object> prefs = new HashMap<String, Object>();
        prefs.put("download.default_directory", projectFolder);
        chromeOptions.setExperimentalOption("prefs", prefs);

        driver = new ChromeDriver(chromeOptions);
    }

    @AfterEach
    public void closeBrowser() {
        driver.quit();
    }

    @Test
    public void customWaitUtilsCanHelpForEverythingNoMaterIfItIsSeleniumOrNot() {
        var url = "https://www.telerik.com/kendo-angular-ui/components/grid/examples/excel/basic/";
        driver.navigate().to(url);

        var wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        var exportButtonLocator = By.cssSelector(".k-grid-excel");
        var exportButton = wait.until(ExpectedConditions.visibilityOfElementLocated(exportButtonLocator));
        exportButton.click();

        await().atMost(10, SECONDS).until(isFileDownloaded(downloadedFilePath));
    }

    private Callable<Boolean> isFileDownloaded(Path downloadedFilePath) {
        return () -> downloadedFilePath.toFile().exists();
    }
}
