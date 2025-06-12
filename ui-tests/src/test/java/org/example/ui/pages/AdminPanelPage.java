package org.example.ui.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class AdminPanelPage {
    private WebDriver driver;

    public AdminPanelPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//*[@id=\"root\"]/div[1]/div/div[2]/div[1]/div/div[1]/div[2]/h6/span[1]")
    private WebElement title;

    public String getTitle() {
        return title.getText();
    }
}