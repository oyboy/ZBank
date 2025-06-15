package org.example.ui.user.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class SportsBookPage {
    private final WebDriver driver;

    public SportsBookPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//*[@id=\"sb\"]/div[2]/div[2]/div[2]/div[3]/div[1]/div")
    private WebElement liveNowTitle;

    public String getLiveNowTitle() {
        waitForVisibility(liveNowTitle, 3);
        return liveNowTitle.getText();
    }

    private void waitForVisibility(WebElement element, int seconds) {
        new WebDriverWait(driver, Duration.ofSeconds(seconds))
                .until(ExpectedConditions.visibilityOf(element));
    }
}
