package org.example.ui.user.pages;

import org.example.ui.base.Waiter;
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

    @FindBy(xpath = "//*[@id=\"sb\"]/div[2]/div[1]/div/div/div[5]/div[2]/div")
    private WebElement coefficientFormatMenu;

    public String getLiveNowTitle() {
        new Waiter(driver).waitForVisibility(liveNowTitle, 3);
        return liveNowTitle.getText();
    }

    public boolean viewAllEventsButtonIsDisplayed() {
        try {
            return viewAllEventsButton.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public List<String> getTopSportsTitles() {
        return topSports.stream().map(WebElement::getText).collect(Collectors.toList());
    }

    public void switchCoefficientAndCheckDisplaying() {
        coefficientFormatMenu.click();
        List<WebElement> elements = coefficientFormatMenu.findElements(By.xpath("./div[2]/div"));
        System.out.println("size: " + elements.size());
        for (WebElement element : elements) {
            String elementText = element.getText();
            System.out.println("text: " + elementText);
            new Waiter(driver).waitForClickable(element, 2);
            element.click();

            String coefficientText = driver.
                    findElement(By.xpath("//*[@id=\"sb\"]/div[2]/div[2]/div[2]/div[7]/div[2]/div/div/div[2]/div[5]/div/div[2]/div[2]/div[2]/div/div[2]/div"))
                    .getText();
            switch (elementText) {
                case "Décimal (2.00)":
                    if (!coefficientText.matches("^[0-9]+\\.[0-9]{1,2}$")) {
                        throw new AssertionError("Неверный формат десятичного коэффициента: " + coefficientText);
                    }
                    break;
                case "Américain (+100)":
                    if (!coefficientText.matches("^[+-][0-9]+$")) {
                        throw new AssertionError("Неверный формат американского коэффициента: " + coefficientText);
                    }
                    break;
            }
            coefficientFormatMenu.click();
        }
    }
}