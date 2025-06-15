package org.example.ui.admin;

import org.example.ui.admin.steps.AdminLanguageSteps;
import org.example.ui.base.BaseAdminPanelTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdminPanelTest extends BaseAdminPanelTest {
    private static AdminLanguageSteps languageSteps;

    @BeforeAll
    public static void setup() {
        BaseAdminPanelTest.setup();
        languageSteps = new AdminLanguageSteps(driver);
    }

    @Test
    public void shouldAddNewLanguageIfMissing(){
        String language = "french";
        languageSteps.addLanguageIfNotExist(language);
        assertTrue(languageSteps.isLanguageVisible(language), "Язык не был добавлен");
    }
}