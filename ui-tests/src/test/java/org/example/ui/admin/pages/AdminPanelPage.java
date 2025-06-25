package org.example.ui.admin.pages;

import org.example.ui.base.Waiter;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class AdminPanelPage {
    private final WebDriver driver;
    private final Waiter waiter;

    /*Language customization*/

    @FindBy(xpath = "//*[@id=\"root\"]/div[1]/div/div[2]/div[1]/div/div[1]/div[2]/h6/span[1]")
    private WebElement title;

    @FindBy(xpath = "//*[@id=\"root\"]/div[1]/div/div[2]/div[3]/div/div/div/div[1]/div[2]/div")
    private WebElement languageButtonsBlock;

    @FindBy(xpath = "//*[@id=\"root\"]/div[1]/div/div[2]/div[3]/div/div/div/div[2]/span/button")
    private WebElement editLanguageCustomizationButton;

    @FindBy(xpath = "//*[@id=\"root\"]/div[1]/div/div[2]/div[3]/div/div/div/div[2]/div/button[2]")
    private WebElement createLanguageButton;

    @FindBy(xpath = "//*[@id=\"root\"]/div[1]/div/div[2]/div[3]/div/div/div/div[2]/div/button[1]")
    private WebElement deleteLanguageButton;

    @FindBy(xpath = "/html/body/div[3]/div[3]/div/div[3]/button[2]")
    private WebElement deleteLanguageInScrollListButton;

    @FindBy(xpath = "/html/body/div[3]/div[3]/div/div[2]/div[3]")
    private WebElement scrollableLanguagesBlock;

    @FindBy(xpath = "/html/body/div[3]/div[3]/div/div[3]/button[2]")
    private WebElement addLanguageButton;

    @FindBy(xpath = "//*[@id=\"root\"]/div[1]/div/div[2]/div[3]/div/div/div/div[2]/div/span/button")
    private WebElement saveLanguageButton;

    @FindBy(xpath = "//*[@id=\"root\"]/div[1]/div/div[2]/div[1]/button")
    private WebElement saveConfigurationButton;

    @FindBy(xpath = "//*[@id=\"root\"]/div[1]/div/div[2]/div[3]/div/div/div/div[2]/div/span/button")
    private WebElement applyButton;

    /* Highlights */

    @FindBy(xpath = "//*[@id=\"root\"]/div[1]/div/div[2]/div[4]/div/div[1]/div[2]/div[1]/div/div/div/div/button")
    private WebElement calendarFromButton;

    @FindBy(xpath = "//*[@id=\"root\"]/div[1]/div/div[2]/div[4]/div/div[1]/div[2]/div[3]/div/div/div/div/button")
    private WebElement calendarToButton;

    @FindBy(xpath = "/html/body/div[3]/div[2]/div/div[2]/div[1]/div[2]/div/div/div[2]/div/div[3]/button[7]")
    private WebElement lastDayOfWeekCalendarButton;

    @FindBy(xpath = "/html/body/div[3]/div[2]/div/div[2]/div[1]/div[2]/div/div/div[2]/div/div[5]/button[1]")
    private WebElement lastDayOfMonthCalendarButton;

    @FindBy(xpath = "//*[@id=\"root\"]/div[1]/div/div[1]/div[2]/div[1]/div[1]/div[1]")
    private WebElement sportsScrollableList;

    @FindBy(xpath = "//*[@id=\"root\"]/div[1]/div/div[1]/div[2]/div[1]/div[5]/span/button")
    private WebElement deleteSportButton;

    @FindBy(xpath = "//*[@id=\"root\"]/div[1]/div/div[2]/div[4]/div/div[2]/div[2]/button")
    private WebElement copyEventsButton;

    public AdminPanelPage(WebDriver driver) {
        this.driver = driver;
        waiter = new Waiter(driver);
        PageFactory.initElements(driver, this);
    }

    public void clickCreateLanguageButton() {
        waiter.waitForVisibility(createLanguageButton, 2);
        createLanguageButton.click();
    }
    public void selectLanguageInList(String language) {
        waiter.waitForVisibility(scrollableLanguagesBlock, 2);
        WebElement element = findLanguageInScrollList(language);
        element.click();
    }
    public void clickEditLanguageCustomizationButton() {
        editLanguageCustomizationButton.click();
    }

    public boolean isLanguagePresent(String language) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        wait.until(driver -> languageButtonsBlock.findElements(By.tagName("button")).size() > 1);

        return languageButtonsBlock.findElements(By.tagName("button")).stream()
                .anyMatch(btn -> btn.getText().equalsIgnoreCase(language));
    }

    public void clickAddLanguageButton() {
        waiter.waitForVisibility(addLanguageButton, 2);
        addLanguageButton.click();
    }

    public void clickSaveLanguageButton() {
        saveLanguageButton.click();
    }

    public void clickSaveConfigurationButton() {
        waiter.waitForClickable(saveConfigurationButton, 2);
        saveConfigurationButton.click();
    }

    private WebElement findLanguageInScrollList(String language) {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        int maxScrollAttempts = 30;
        int previousItemCount = 0;

        for (int attempt = 0; attempt < maxScrollAttempts; attempt++) {
            List<WebElement> items = scrollableLanguagesBlock.findElements(By.xpath("./div"));
            for (WebElement item : items) {
                if (item.getText().equalsIgnoreCase(language)) {
                    return item;
                }
            }
            js.executeScript("arguments[0].scrollTop += arguments[0].offsetHeight;", scrollableLanguagesBlock);

            int currentItemCount = items.size();
            if (currentItemCount == previousItemCount) {
                break;
            }
            previousItemCount = currentItemCount;
        }
        throw new NoSuchElementException("Язык \"" + language + "\" не найден в списке после прокрутки.");
    }

    public void clickDeleteLanguageButton() {
        waiter.waitForClickable(deleteLanguageButton, 2);
        deleteLanguageButton.click();
    }
    public void removeLanguageFromList() {
        deleteLanguageInScrollListButton.click();
    }
    public void clickApplyButton(){
        waiter.waitForClickable(applyButton, 2);
        applyButton.click();
    }

    public void selectLanguage(String language) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        wait.until(driver -> languageButtonsBlock.findElements(By.tagName("button")).size() > 1);

        languageButtonsBlock.findElements(By.tagName("button")).stream()
                .filter(btn -> btn.getText().equalsIgnoreCase(language))
                .findFirst().ifPresent(WebElement::click);
    }

    public int getCountOfAddedLanguages(){
        return languageButtonsBlock.findElements(By.tagName("button")).size();
    }

    public void selectDate() {
        calendarToButton.click();
        waiter.waitForClickable(lastDayOfWeekCalendarButton, 2);
        lastDayOfWeekCalendarButton.click();
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.err.println("Can't select date: " + e.getMessage());
        }
    }

    public void selectFirstHighlightsInSportsScrollList() {
        sportsScrollableList.click();

        WebElement firstNestedList = sportsScrollableList.findElement(By.xpath("../div[2]/div/div/div[1]"));
        firstNestedList.click();

        WebElement firstListItem = firstNestedList.findElement(By.xpath("./div[2]/div/div/div"));
        firstListItem.click();
    }

    public int getCountOfAddedHighlights() {
        List<WebElement> addedHighlights = driver.findElements(By.xpath("//*[@id=\"root\"]/div[1]/div/div[2]/div[5]/div/div[1]/div"));
        return addedHighlights.size();
    }
    public List<String> getAddedHighlights() {
        return driver.findElements(By.xpath("//*[@id=\"root\"]/div[1]/div/div[2]/div[5]/div/div[1]/div"))
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public void selectEvents(int count) {
        for (int i = 1; i <= count; i++) {
            By buttonLocator = By.xpath("(//*[@id='root']/div[1]/div/div[2]/div[4]/div/div[2]//div)[1]//button");
            WebElement button = driver.findElement(buttonLocator);
            button.click();
        }
    }

    public int getCountOfAddedSports(){
        By sportsLocator = By.xpath("//*[@id=\"root\"]/div[1]/div/div[1]/div[2]/div[1]/div");
        List<WebElement> sports = driver.findElements(sportsLocator);
        return sports.size();
    }

    public String selectCheckBoxOfFirstSportAndDelete(){
        sportsScrollableList.click();
        String title = sportsScrollableList.findElement(By.xpath("./div[1]")).getText();

        WebElement checkbox = sportsScrollableList.findElement(By.tagName("span"));
        checkbox.click();

        waiter.waitForClickable(deleteSportButton, 2);
        deleteSportButton.click();

        return title;
    }

    public void selectIncorrectDate(){
        calendarFromButton.click();
        waiter.waitForClickable(lastDayOfMonthCalendarButton, 2);
        lastDayOfMonthCalendarButton.click();
    }

    public String getInvalidFormatMessage(){
       WebElement element = driver.findElement(By.xpath("//*[@id=\":ro:\"]"));
        waiter.waitForVisibility(element, 2);
       return element.getText();
    }

    public void clickCopyEventsButton(){
        waiter.waitForClickable(copyEventsButton, 2);
        copyEventsButton.click();
    }
}