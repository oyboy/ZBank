package org.example.ui.admin;

import org.example.ui.admin.steps.AdminLanguageSteps;
import org.example.ui.admin.steps.HighlightsSteps;
import org.example.ui.base.BaseAdminPanelTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdminPanelTest extends BaseAdminPanelTest {
    private static AdminLanguageSteps languageSteps;
    private static HighlightsSteps highlightsSteps;

    @BeforeAll
    public static void setup() {
        BaseAdminPanelTest.setup();
        languageSteps = new AdminLanguageSteps(driver);
        highlightsSteps = new HighlightsSteps(driver);
    }

    @Test
    public void shouldAddNewLanguageIfMissing(){
        String language = "french";
        languageSteps.addLanguageIfNotExist(language);
        assertTrue(languageSteps.isLanguageVisible(language), "Язык не был добавлен");
    }

    @Test
    public void shouldAddNewHighlightsIfMissing(){
        String language = "french";
        int count = 11;
        highlightsSteps.addHighlights(language, count);
        assertTrue(highlightsSteps.getCountOfAddedHighlights() > count, "Добавлено неверное количество событий");
    }

    @Test
    public void shouldRemoveLanguageIfExist(){
        String language = "french";
        languageSteps.removeLanguageIfExists(language);
        assertFalse(languageSteps.isLanguageVisible(language), "Язык не был удалён");
    }
}