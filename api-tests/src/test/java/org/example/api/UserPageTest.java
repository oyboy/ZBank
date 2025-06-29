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
    @DisplayName("Проверка перевода элементов страницы")
    @Description("Проверяет, что запрос возвращает корректные переводы статических элементов: Today, Stake, No bets found")
    @Story("Frontend_Переводы")
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
    @DisplayName("Скрытие кнопки 'View All Events'")
    @Description("Кнопка 'View All Events' не отображается, если количество событий меньше 10")
    @Story("Frontend_Показ событий")
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

        assertThat("Количество событий должно быть меньше 10", response.getResult().getEventsCount(), lessThan(10));
        assertFalse(response.getResult().getShowMoreEvents(), "Кнопка показа всех событий не должна отображаться");
    }

    @Test
    @DisplayName("Отображение кнопки 'View All Events'")
    @Description("Кнопка 'View All Events' отображается, если количество событий больше или равно 10")
    @Story("Frontend_Показ событий")
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

        assertThat("Количество событий должно быть больше или равно 10", response.getResult().getEventsCount(), greaterThanOrEqualTo(10));
        assertTrue(response.getResult().getShowMoreEvents(), "Кнопка показа всех событий должна отображаться");
    }

    @Test
    @DisplayName("Получение хайлайтов")
    @Description("Метод GetHighlights при вызове возвращает непустой список событий")
    @Story("Frontend_Хайлайты")
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
        assertThat("Количество хайлайтов должно быть больше 0", response.getResult().getEventsCount(), greaterThanOrEqualTo(1));
    }

    @Test
    @DisplayName("Получение избранных чемпионатов")
    @Description("При корректном диапазоне дат метод возвращает непустой список избранных чемпионатов")
    @Story("Frontend_Избранные чемпионаты")
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
        assertFalse(response.getResult().isEmpty(), "Список событий не должен пуст при корректной дате");
    }

    @Test
    @DisplayName("Запрос избранных чемпионатов с некорректной датой")
    @Description("Проверяет, что при передаче даты из прошлого возвращается ошибка 400 и пустой список чемпионатов")
    @Story("Frontend_Избранные чемпионаты")
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
        assertTrue(response.getResult().isEmpty(), "Список событий должен быть пуст при некорректной дате");
    }
}