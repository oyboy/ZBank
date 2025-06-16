package org.example.ui.admin;

import io.qameta.allure.Description;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
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
    @Description("Проверка добавления языка в список 'Language customization'")
    @Story("Admin_Языковая настройка_Добавление языка")
    @TmsLink("TC_03_1")
    public void shouldAddNewLanguageIfNotPresent() {
        String language = "french";
        languageSteps.addLanguageIfNotExist(language);
        assertTrue(languageSteps.isLanguageVisible(language), "Язык не был добавлен");
    }

    @Test
    @Description("Проверка добавления новых событий, если их недостаточно")
    @Story("Highlights_Добавление событий")
    @TmsLink("TC_04_1")
    public void shouldAddNewHighlightsIfNotSufficient() {
        String language = "french";
        int count = 11;
        highlightsSteps.addHighlights(language, count);
        assertTrue(highlightsSteps.getCountOfAddedHighlights() > count, "Добавлено неверное количество событий");
    }

    @Test
    @Description("Проверка удаления языка из списка 'Language customization'")
    @Story("Admin_Языковая настройка_Удаление языка")
    @TmsLink("TC_03_2")
    public void shouldRemoveLanguageIfPresent() {
        String language = "french";
        languageSteps.removeLanguageIfExists(language);
        assertFalse(languageSteps.isLanguageVisible(language), "Язык не был удалён");
    }

    @Test
    @Description("Проверка удаления спорта из списка")
    @Story("Highlights_Удаление спорта")
    @TmsLink("TC_04_2")
    public void shouldRemoveFirstSportIfPresent() {
        int countOfSports = highlightsSteps.getCountOfAddedSports();
        highlightsSteps.deleteFirstSport();
        assertEquals(highlightsSteps.getCountOfAddedSports(), countOfSports - 1);
    }

    @Test
    @Description("Проверка отображения сообщения об ошибке для неверного формата даты")
    @Story("Highlights_Неверный формат даты")
    @TmsLink("TC_06")
    public void shouldDisplayErrorMessageForInvalidDateFormat() {
        String message = highlightsSteps.setIncorrectDate();
        assertEquals("Invalid Date Format", message, "Должно отображаться корректное сообщение");
    }

    @Test
    @Description("Проверка копирования событий с одного языка на другой")
    @Story("Admin_Копирование событий")
    @TmsLink("TC_07")
    public void shouldCopyEventsFromDefaultLanguageToAnotherLanguage() {
        String language = "french";
        languageSteps.addLanguageIfNotExist(language);
        List<String> eventsBefore = highlightsSteps.getAddedHighlights("default");

        highlightsSteps.copyEvents(language);
        List<String> eventsAfter = highlightsSteps.getAddedHighlights(language);

        assertTrue(eventsAfter.containsAll(eventsBefore), "Не все старые события были скопированы");
        assertTrue(eventsAfter.size() >= eventsBefore.size(), "Количество событий не увеличилось");
    }
}
