import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.List;

public class GoogleAutoSearch {
    public static void main(String[] args) {
        // Configurar ChromeDriver automáticamente
        //WebDriverManager.chromedriver().setup();
        System.setProperty("webdriver.chrome.driver", "/Users/angelesbuild38/Documents/Chromedriver/chromedriver");

        // Opciones de Chrome
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--start-maximized");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-gpu");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36");

        // Iniciar WebDriver
        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        try {
            // 1. Ir a Google
            driver.get("https://www.google.com");

            // Manejar pop-up de cookies
            try {
                WebElement acceptCookies = wait.until(ExpectedConditions.elementToBeClickable(By.id("L2AGLb")));
                acceptCookies.click();
                System.out.println("Cookies aceptadas.");
            } catch (Exception e) {
                System.out.println("No se encontró el botón de aceptar cookies.");
            }

            // 2. Esperar a que la barra de búsqueda sea interactuable
            WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.name("q")));

            // Alternativa si no funciona con sendKeys normal
            Actions actions = new Actions(driver);
            actions.moveToElement(searchBox).click().sendKeys("automatización").sendKeys(Keys.RETURN).perform();

            // 3. Encontrar el enlace de Wikipedia
            List<WebElement> results = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("h3")));
            WebElement wikipediaLink = null;

            for (WebElement result : results) {
                if (result.getText().contains("Wikipedia")) {
                    wikipediaLink = result.findElement(By.xpath("./ancestor::a"));
                    break;
                }
            }

            if (wikipediaLink != null) {
                wikipediaLink.click();
            } else {
                System.out.println("No se encontró un enlace de Wikipedia en los resultados.");
                return;
            }

            // 4. Buscar el año del primer proceso automático en la página de Wikipedia
            String pageText = driver.findElement(By.tagName("body")).getText();
            String yearFound = pageText.replaceAll("[^0-9]", " ").trim().split(" ")[0]; // Busca el primer número

            System.out.println("El primer proceso automático se realizó en el año: " + yearFound);

            // 5. Tomar una captura de pantalla
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destinationFile = new File("wikipedia_screenshot.png");
            Files.copy(screenshot.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            System.out.println("Captura de pantalla guardada en: " + destinationFile.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Cerrar el navegador
            driver.quit();
        }
    }
}
