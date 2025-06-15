package org.example.ui.admin.steps;

import org.example.ui.admin.pages.AdminPanelPage;
import org.openqa.selenium.WebDriver;

public class AdminLanguageSteps {
    private final AdminPanelPage adminPage;

    public AdminLanguageSteps(WebDriver driver) {
        this.adminPage = new AdminPanelPage(driver);
    }

    public void addLanguageIfNotExist(String language) {
        if (adminPage.getCountOfAddedLanguages() > 9) throw new RuntimeException("Достигнут лимит добавления языков");

        boolean exists = adminPage.isLanguagePresent(language);
        if (!exists){
            adminPage.clickEditLanguageCustomizationButton();
            adminPage.clickCreateLanguageButton();
            adminPage.selectLanguageInList(language);
            adminPage.clickAddLanguageButton();
            adminPage.clickSaveLanguageButton();
            adminPage.clickSaveConfigurationButton();
        }
    }

    public boolean isLanguageVisible(String language) {
        return adminPage.isLanguagePresent(language);
    }

    public void removeLanguageIfExists(String language) {
        if (adminPage.isLanguagePresent(language)) {
            adminPage.selectLanguage(language);
            adminPage.clickEditLanguageCustomizationButton();
            adminPage.clickDeleteLanguageButton();
            adminPage.selectLanguageInList(language);
            adminPage.removeLanguageFromList();
            adminPage.clickSaveConfigurationButton();
        }
    }
}
