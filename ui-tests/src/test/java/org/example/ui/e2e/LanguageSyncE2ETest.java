package org.example.ui.e2e;

import org.example.ui.admin.steps.AdminLanguageSteps;
import org.example.ui.admin.steps.HighlightsSteps;
import org.example.ui.base.BaseE2ETest;
import org.example.ui.user.steps.SportsBookSteps;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LanguageSyncE2ETest extends BaseE2ETest {
    private AdminLanguageSteps adminSteps;
    private SportsBookSteps userSteps;
    private HighlightsSteps highlightsSteps;

    @BeforeAll
    public static void init() {
        setup();
        loginToAdmin();
    }
    @BeforeEach
    public void setUp() {
        navigateToConfig();
        adminSteps = new AdminLanguageSteps(driver);
        highlightsSteps = new HighlightsSteps(driver);
        userSteps = new SportsBookSteps(driver);
    }

    @Test
    public void shouldAddLanguageAndSeeItInUserUI() {
        String language = "french";
        adminSteps.addLanguageIfNotExist(language);
        goToUserSite();

        boolean isCorrectLanguage = userSteps.isLiveNowTextCorrect("En direct maintenant");
        assertTrue(isCorrectLanguage, "Текст отображается не на нужном языке");
    }

    @Test
    public void checkIfAllEventsButtonIsDisplayed() {
        int count = 11;
        highlightsSteps.addHighlights("french", count);
        goToUserSite();

        if (count >= 10) {
            assertTrue(userSteps.allEventsButtonIsDisplayed(), "Кнопка должна отображаться, если событий >= 10");
        } else {
            assertFalse(userSteps.allEventsButtonIsDisplayed(), "Кнопка не должна отображаться, если событий < 10");
        }
    }
}
