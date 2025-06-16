package org.example.ui.user;

import io.qameta.allure.Description;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import org.example.ui.base.BaseSportsBookTest;
import org.example.ui.user.steps.SportsBookSteps;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SportsBookTest extends BaseSportsBookTest {
    private static SportsBookSteps sportsBookSteps;

    @BeforeAll
    public static void setup() {
        BaseSportsBookTest.setup();
        sportsBookSteps = new SportsBookSteps(driver);
    }

    @Test
    @Description("Проверка, что на сайте отображается французский язык")
    @TmsLink("TC_01")
    public void shouldDisplayLiveNowTextInExpectedLanguage() {
        String expectedTitle = "En direct maintenant";
        assertTrue(
                sportsBookSteps.isLiveNowTextCorrect(expectedTitle),
                "Текст отображается на неверном языке"
        );
    }

    @Test
    @Description("Проверка корректности отображения кнопки 'View all events'")
    @Story("UI_Кнопка 'View all events'")
    @TmsLink("TC_02")
    public void shouldDisplayAllEventsButton_whenCountIsGreaterThanOrEqualToTen() {
        assertTrue(sportsBookSteps.allEventsButtonIsDisplayed(),
                "Кнопка view all events должна отображаться, если событий >= 10");
    }

    @Test
    @Description("Проверка правильности отображения при изменении вида коэффициента")
    @Story("SportsBook_Отображение коэффициентов")
    @TmsLink("TC_05")
    public void shouldCheckCoefficientsInSportsList() {
        sportsBookSteps.checkCoefficientInSportsList();
    }
}