package org.example.api;

import clients.GenericRequest;
import clients.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import org.joda.time.DateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.*;

public class UserPageTest extends BaseTest {
    @Test
    @DisplayName("Check translation of page elements")
    @Description("Checks that the request returns correct translations for static elements: Today, Stake, No bets found")
    @Story("Frontend_Translations")
    @TmsLink("TC_USER_01")
    public void getLanguagesList() {
        GenericRequest request = GenericRequest.builder()
                .configId(1)
                .integration("skintest")
                .skinName("betsonic")
                .langId(8)
                .timezoneOffset(-180)
                .deviceType("Desktop")
                .countryCode("")
                .numformat("en")
                .culture("fr-fr")
                .skinName("betsonic")
                .build();

        var response = UserClient.getStaticTranslations(request);
        Map<String, String> res = response.getResult();

        assertFalse(res.isEmpty(), "Translation list should not be empty");
        assertEquals("Aujourd’hui", res.get("Today"), "Translation for 'Today' is incorrect");
        assertEquals("Mise", res.get("Stake"), "Translation for 'Stake' is incorrect");
        assertEquals("Paris introuvables", res.get("No bets found"), "Translation for 'No bets found' is incorrect");
    }

    @Test
    @DisplayName("Hide 'View All Events' button")
    @Description("The 'View All Events' button is not displayed if the number of events is less than 10")
    @Story("Frontend_Show Events")
    @TmsLink("TC_USER_02")
    public void checkViewAllEventsButtonIsNotDisplayed() {
        GenericRequest request = GenericRequest.builder()
                .configId(1)
                .integration("skintest")
                .skinName("betsonic")
                .langId(8)
                .timezoneOffset(-180)
                .deviceType("Desktop")
                .countryCode("RU")
                .culture("en-gb")
                .numformat("en")
                .sportId(70)
                .count(10)
                .showAllEvents(false)
                .hasStreaming(false)
                .build();

        var response = UserClient.getUpcoming(request);

        assertThat("Number of events should be less than 10", response.getResult().getEventsCount(), lessThan(10));
        assertFalse(response.getResult().getShowMoreEvents(), "The 'View All Events' button should not be displayed");
    }

    @Test
    @DisplayName("Display 'View All Events' button")
    @Description("The 'View All Events' button is displayed if the number of events is greater than or equal to 10")
    @Story("Frontend_Show Events")
    @TmsLink("TC_USER_03")
    public void checkViewAllEventsButtonIsDisplayed() {
        GenericRequest request = GenericRequest.builder()
                .configId(1)
                .integration("skintest")
                .skinName("betsonic")
                .langId(8)
                .timezoneOffset(-180)
                .deviceType("Desktop")
                .countryCode("RU")
                .culture("en-gb")
                .numformat("en")
                .sportId(74)
                .count(10)
                .showAllEvents(false)
                .hasStreaming(false)
                .build();

        var response = UserClient.getUpcoming(request);

        assertThat("Number of events should be greater than or equal to 10", response.getResult().getEventsCount(), greaterThanOrEqualTo(10));
        assertTrue(response.getResult().getShowMoreEvents(), "The 'View All Events' button should be displayed");
    }

    @Test
    @DisplayName("Retrieve highlights")
    @Description("The GetHighlights method returns a non-empty list of events when called")
    @Story("Frontend_Highlights")
    @TmsLink("TC_USER_04")
    public void callingGetHighlightsReturnsNonEmptyEventList() {
        GenericRequest request = GenericRequest.builder()
                .configId(1)
                .integration("skintest")
                .skinName("betsonic")
                .langId(8)
                .timezoneOffset(420)
                .deviceType("Desktop")
                .countryCode("RU")
                .culture("en-gb")
                .numformat("en")
                .sportId(76)
                .count(10)
                .showAllEvents(false)
                .build();

        var response = UserClient.getHighlights(request);
        assertThat("Number of highlights should be greater than 0", response.getResult().getEventsCount(), greaterThanOrEqualTo(1));
    }

    @Test
    @DisplayName("Retrieve favourite championships")
    @Description("When the date range is correct, the method returns a non-empty list of favourite championships")
    @Story("Frontend_Favourite Championships")
    @TmsLink("TC_USER_05")
    public void getFavouriteChampsWithCorrectDate() {
        DateTime startDate = DateTime.now();
        DateTime endDate = DateTime.now().plusDays(1);

        GenericRequest request = GenericRequest.builder()
                .configId(1)
                .integration("skintest")
                .skinName("betsonic")
                .langId(8)
                .timezoneOffset(-180)
                .deviceType("Desktop")
                .countryCode("RU")
                .culture("en-gb")
                .numformat("en")
                .period("periodmonth")
                .startDate(startDate)
                .endDate(endDate)
                .build();

        var response = UserClient.getFavouritesChamps(request);
        assertFalse(response.getResult().isEmpty(), "Event list should not be empty with valid dates");
    }

    @Test
    @DisplayName("Request favourite championships with incorrect date")
    @Description("Checks that when a past date is provided, an error 400 is returned and the list of championships is empty")
    @Story("Frontend_Favourite Championships")
    @TmsLink("TC_USER_06")
    public void getFavouriteChampsWithUncorrectDate() {
        DateTime startDate = DateTime.now();
        DateTime endDate = DateTime.now().minusDays(1);

        GenericRequest request = GenericRequest.builder()
                .configId(1)
                .integration("skintest")
                .skinName("betsonic")
                .langId(8)
                .timezoneOffset(-180)
                .deviceType("Desktop")
                .countryCode("RU")
                .culture("en-gb")
                .numformat("en")
                .period("periodmonth")
                .startDate(startDate)
                .endDate(endDate)
                .build();

        var response = UserClient.getFavouritesChamps(request);
        assertTrue(response.getResult().isEmpty(), "The event list should be empty with incorrect dates");
    }
}