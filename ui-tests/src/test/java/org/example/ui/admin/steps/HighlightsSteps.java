package org.example.ui.admin.steps;

import org.example.ui.admin.pages.AdminPanelPage;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class HighlightsSteps {
    private final AdminPanelPage adminPanelPage;

    public HighlightsSteps(WebDriver driver) {
        this.adminPanelPage = new AdminPanelPage(driver);
    }

    public void addHighlights(String language, int count){
        adminPanelPage.selectLanguage(language);
        if (adminPanelPage.getCountOfAddedHighlights() < count) {
            adminPanelPage.selectDate();
            adminPanelPage.selectFirstHighlightsInSportsScrollList();
            adminPanelPage.selectEvents(count);
            adminPanelPage.clickSaveConfigurationButton();
        }
    }

    public int getCountOfAddedHighlights(){
        return adminPanelPage.getCountOfAddedHighlights();
    }
    public List<String> getAddedHighlights(String language){
        adminPanelPage.selectLanguage(language);
        return adminPanelPage.getAddedHighlights();
    }

    public String deleteFirstSport(){
        if (adminPanelPage.getCountOfAddedSports() == 0)
            throw new RuntimeException("Нужно добавить хотя бы один вид спорта для удаления");
        String sportForRemoving = adminPanelPage.selectCheckBoxOfFirstSportAndDelete();
        adminPanelPage.clickSaveConfigurationButton();
        return sportForRemoving;
    }

    public int getCountOfAddedSports(){
        return adminPanelPage.getCountOfAddedSports();
    }

    public String setIncorrectDate(){
        adminPanelPage.selectIncorrectDate();
        return adminPanelPage.getInvalidFormatMessage();
    }

    public void copyEvents(String toLanguage){
        addHighlights("Default", 1);
        adminPanelPage.selectLanguage(toLanguage);
        adminPanelPage.clickCopyEventsButton();
        adminPanelPage.clickSaveConfigurationButton();
    }
}