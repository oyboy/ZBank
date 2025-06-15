package org.example.ui.admin.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class AdminPanelNavigationPage {
    private WebDriver driver;

    @FindBy(xpath = "//*[@id=\"sidebar-menu\"]/div/ul/li[2]")
    private WebElement skinManagementButton;

    @FindBy(xpath = "//*[@id=\"sidebar-menu\"]/div/ul/li[2]/ul/li[1]/a")
    private WebElement highlightsManagerButton;

    @FindBy(xpath = "//*[@id=\"root\"]/div[1]/div/div[2]/div/div/div[1]/div/div")
    private WebElement configButton;

    public AdminPanelNavigationPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void navigateToConfig() {
        waitForClickable(skinManagementButton, 2);
        skinManagementButton.click();

        waitForVisibility(highlightsManagerButton, 2);
        highlightsManagerButton.click();

        waitForVisibility(configButton, 2);
        configButton.click();
    }

    private void waitForVisibility(WebElement element, int seconds) {
        new WebDriverWait(driver, Duration.ofSeconds(seconds))
                .until(ExpectedConditions.visibilityOf(element));
    }

    private void waitForClickable(WebElement element, int seconds) {
        new WebDriverWait(driver, Duration.ofSeconds(seconds))
                .until(ExpectedConditions.elementToBeClickable(element));
    }
}
