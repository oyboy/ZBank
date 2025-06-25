package org.example.ui.base;

import org.example.ui.admin.pages.AdminPanelNavigationPage;
import org.example.ui.admin.pages.LoginPage;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;

public abstract class BaseE2ETest {
    protected static WebDriver driver;

    @BeforeAll
    public static void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    protected static void loginToAdmin() {
        driver.get(ConfProperties.getProperty("login_page"));

        LoginPage loginPage = new LoginPage(driver);
        loginPage.authenticate(
                ConfProperties.getProperty("login"),
                ConfProperties.getProperty("password")
        );
    }

    protected static void navigateToConfig(){
        driver.get(ConfProperties.getProperty("config_page"));
        AdminPanelNavigationPage navigation = new AdminPanelNavigationPage(driver);
        navigation.navigateToConfig();
    }

    protected static void goToUserSite() {
        driver.get(ConfProperties.getProperty("sports_book_page"));
    }
}