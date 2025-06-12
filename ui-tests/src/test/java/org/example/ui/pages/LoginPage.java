package org.example.ui.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {
    private WebDriver driver;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//*[@id=\"accept-cookie-btn\"]")
    private WebElement cookieButton;

    @FindBy(xpath = "//*[@id=\"username_input\"]")
    private WebElement login_field;

    @FindBy(xpath = "//*[@id=\"password_input\"]")
    private WebElement password_field;

    @FindBy(xpath = "//*[@id=\"login-button\"]")
    private WebElement login_button;

    public void authenticate(String username, String password) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(cookieButton));
        cookieButton.click();

        login_field.sendKeys(username);
        password_field.sendKeys(password);
        login_button.click();
    }
}
