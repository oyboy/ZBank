package org.example.ui.user.steps;

import org.example.ui.user.pages.SportsBookPage;
import org.openqa.selenium.WebDriver;

public class SportsBookSteps {
    private final SportsBookPage sportsBookPage;

    public SportsBookSteps(WebDriver driver) {
        this.sportsBookPage = new SportsBookPage(driver);
    }

    public boolean isLiveNowTextCorrect(String expectedText) {
        String actual = sportsBookPage.getLiveNowTitle();
        return expectedText.equalsIgnoreCase(actual);
    }

    public boolean allEventsButtonIsDisplayed() {
        return sportsBookPage.viewAllEventsButtonIsDisplayed();
    }
}
