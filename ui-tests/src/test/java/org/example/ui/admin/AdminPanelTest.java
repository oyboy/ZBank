package org.example.ui.admin;

import org.example.ui.admin.steps.AdminLanguageSteps;
import org.example.ui.admin.steps.HighlightsSteps;
import org.example.ui.base.BaseAdminPanelTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
    public void shouldAddNewLanguageIfNotPresent() {
        String language = "french";
        languageSteps.addLanguageIfNotExist(language);
        assertTrue(languageSteps.isLanguageVisible(language), "Язык не был добавлен");
    }

    @Test
    public void shouldAddNewHighlightsIfNotSufficient() {
        String language = "french";
        int count = 11;
        highlightsSteps.addHighlights(language, count);
        assertTrue(highlightsSteps.getCountOfAddedHighlights() > count, "Добавлено неверное количество событий");
    }

    @Test
    public void shouldRemoveLanguageIfPresent() {
        String language = "french";
        languageSteps.removeLanguageIfExists(language);
        assertFalse(languageSteps.isLanguageVisible(language), "Язык не был удалён");
    }

    @Test
    public void shouldRemoveFirstSportIfPresent() {
        int countOfSports = highlightsSteps.getCountOfAddedSports();
        highlightsSteps.deleteFirstSport();
        assertEquals(highlightsSteps.getCountOfAddedSports(), countOfSports - 1);
    }

    @Test
    public void shouldDisplayErrorMessageForInvalidDateFormat() {
        String message = highlightsSteps.setIncorrectDate();
        assertEquals("Invalid Date Format", message, "Должно отображаться корректное сообщение");
    }

    @Test
    public void shouldCopyEventsFromDefaultLanguageToAnotherLanguage() {
        String language = "french";
        languageSteps.addLanguageIfNotExist(language);
        List<String> eventsBefore = highlightsSteps.getAddedHighlights(language);

        highlightsSteps.copyEvents(language);
        List<String> eventsAfter = highlightsSteps.getAddedHighlights(language);

        assertTrue(eventsAfter.containsAll(eventsBefore), "Не все старые события были скопированы");
        assertTrue(eventsAfter.size() >= eventsBefore.size(), "Количество событий не увеличилось");
    }
}
