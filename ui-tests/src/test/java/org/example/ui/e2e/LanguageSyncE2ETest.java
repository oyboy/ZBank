package org.example.ui.e2e;

import org.example.ui.admin.steps.AdminLanguageSteps;
import org.example.ui.base.BaseE2ETest;
import org.example.ui.user.steps.SportsBookSteps;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LanguageSyncE2ETest extends BaseE2ETest {
    private static AdminLanguageSteps adminSteps;
    private static SportsBookSteps userSteps;

    @BeforeAll
    public static void init() {
        setup();
        loginToAdmin();

        adminSteps = new AdminLanguageSteps(driver);
        adminSteps.addLanguageIfNotExist("french");

        goToUserSite();
        userSteps = new SportsBookSteps(driver);
    }

    @Test
    public void shouldAddLanguageAndSeeItInUserUI() {
        boolean isCorrectLanguage = userSteps.isLiveNowTextCorrect(
                "En direct maintenant"
        );
        assertTrue(isCorrectLanguage, "Текст отображается не на нужном языке");
    }
}
