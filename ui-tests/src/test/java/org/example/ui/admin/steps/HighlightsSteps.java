package org.example.ui.admin.steps;

import org.example.ui.admin.pages.AdminPanelPage;
import org.openqa.selenium.WebDriver;

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
}