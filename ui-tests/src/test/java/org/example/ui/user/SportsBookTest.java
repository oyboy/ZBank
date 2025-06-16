package org.example.ui.user;

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
    public void shouldDisplayLiveNowTextInExpectedLanguage() {
        String expectedTitle = "En direct maintenant";
        assertTrue(
                sportsBookSteps.isLiveNowTextCorrect(expectedTitle),
                "Текст отображается на неверном языке"
        );
    }

    @Test
    public void shouldDisplayViewAllEventsButtonWhenEventsCountIsGreaterThanOrEqualToTen() {
        assertTrue(sportsBookSteps.allEventsButtonIsDisplayed(),
                "Кнопка view all events должна отображаться, если событий >= 10");
    }

    @Test
    public void shouldCheckCoefficientsInSportsList() {
        sportsBookSteps.checkCoefficientInSportsList();
    }
}