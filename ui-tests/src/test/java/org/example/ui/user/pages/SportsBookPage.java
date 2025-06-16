package org.example.ui.user.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class SportsBookPage {
    private final WebDriver driver;

    public SportsBookPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//*[@id=\"sb\"]/div[2]/div[2]/div[2]/div[3]/div[1]/div")
    private WebElement liveNowTitle;

    @FindBy(css = "#sb > div.asb-flex._asb_view-prelive > div.asb-flex-col._asb_page-column-center > div:nth-child(2) > div > div.asb-flex-cc._asb_redirect-button-events-by-type")
    private WebElement viewAllEventsButton;

    @FindBy(xpath = "//*[@id=\"sb\"]/div[2]/div[1]/div[2]/div[10]/div[1]/div[2]/div/div/div[2]/div//div")
    private List<WebElement> topSports;

    public String getLiveNowTitle() {
        waitForVisibility(liveNowTitle, 3);
        return liveNowTitle.getText();
    }

    public boolean viewAllEventsButtonIsDisplayed() {
        try {
            return viewAllEventsButton.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private void waitForVisibility(WebElement element, int seconds) {
        new WebDriverWait(driver, Duration.ofSeconds(seconds))
                .until(ExpectedConditions.visibilityOf(element));
    }
    private void waitForClickable(WebElement element, int seconds) {
        new WebDriverWait(driver, Duration.ofSeconds(seconds))
                .until(ExpectedConditions.elementToBeClickable(element));
    }

    public List<String> getTopSportsTitles() {
        return topSports.stream().map(WebElement::getText).collect(Collectors.toList());
    }
}
