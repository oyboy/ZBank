package org.example.api;

import clients.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import models.requests.GenericRequest;
import models.responses.FavouritesChampsResponse;
import models.responses.HighlightsResponse;
import models.responses.StaticTranslationsResponse;
import models.responses.UpcomingResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.*;

public class UserPageTest extends BaseTest {
    private GenericRequest.GenericRequestBuilder baseRequestBuilder() {
        return GenericRequest.builder()
                .timezoneOffset(420)
                .langId(39)
                .skinName("betsonic")
                .configId(1)
                .culture("fr-fr")
                .countryCode("RU")
                .deviceType("Desktop")
                .numformat("en")
                .integration("skintest")
                .showAllEvents(false)
                .count(10)
                .hasStreaming(false);
    }
    @Test
    @DisplayName("Проверка перевода элементов страницы")
    @Description("Проверяет, что запрос возвращает корректные переводы статических элементов: Today, Stake, No bets found")
    @Story("Frontend_Переводы")
    @TmsLink("TC_USER_01")
    public void getLanguagesList() {
        GenericRequest request = baseRequestBuilder()
                .timezoneOffset(-180)
                .langId(8)
                .countryCode("")
                .build();

        StaticTranslationsResponse response = UserClient.getStaticTranslations(request);
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
        GenericRequest request = baseRequestBuilder()
                .sportId(74)
                .build();

        UpcomingResponse response = UserClient.getUpcoming(request);

        assertThat("Количество событий должно быть меньше 10", response.getResult().getEventsCount(), lessThan(10));
        assertFalse(response.getResult().isShowMoreEvents(), "Кнопка показа всех событий не должна отображаться");
    }

    @Test
    @DisplayName("Отображение кнопки 'View All Events'")
    @Description("Кнопка 'View All Events' отображается, если количество событий больше или равно 10")
    @Story("Frontend_Показ событий")
    @TmsLink("TC_USER_03")
    public void checkViewAllEventsButtonIsDisplayed() {
        GenericRequest request = baseRequestBuilder()
                .sportId(76)
                .build();

        UpcomingResponse response = UserClient.getUpcoming(request);

        assertThat("Количество событий должно быть больше или равно 10", response.getResult().getEventsCount(), greaterThanOrEqualTo(10));
        assertTrue(response.getResult().isShowMoreEvents(), "Кнопка показа всех событий должна отображаться");
    }

    @Test
    @DisplayName("Получение хайлайтов")
    @Description("Метод GetHighlights при вызове возвращает непустой список событий")
    @Story("Frontend_Хайлайты")
    @TmsLink("TC_USER_04")
    public void callingGetHighlightsReturnsNonEmptyEventList() {
        GenericRequest request = baseRequestBuilder()
                .langId(8)
                .culture("en-gb")
                .sportId(76)
                .build();

        HighlightsResponse response = UserClient.getHighlights(request);

        assertThat("Количество хайлайтов должно быть больше 0", response.getResult().getEventsCount(), greaterThanOrEqualTo(1));
    }

    @Test
    @DisplayName("Получение избранных чемпионатов")
    @Description("При корректном диапазоне дат метод возвращает непустой список избранных чемпионатов")
    @Story("Frontend_Избранные чемпионаты")
    @TmsLink("TC_USER_05")
    public void getFavouriteChampsWithCorrectDate() {
        Instant startDate = Instant.now();
        Instant endDate = Instant.now().atZone(ZoneId.systemDefault()).plusMonths(1).toInstant();

        GenericRequest request = baseRequestBuilder()
                .period("periodmonth")
                .startDate(startDate)
                .endDate(endDate)
                .build();

        FavouritesChampsResponse response = UserClient.getFavouritesChamps(request);

        List<FavouritesChampsResponse.Result> events = response.getResult();
        assertFalse(events.isEmpty(), "Список событий не должен пуст при корректной дате");
    }

    @Test
    @DisplayName("Запрос избранных чемпионатов с некорректной датой")
    @Description("Проверяет, что при передаче даты из прошлого возвращается ошибка 400 и пустой список чемпионатов")
    @Story("Frontend_Избранные чемпионаты")
    @TmsLink("TC_USER_06")
    public void getFavouriteChampsWithUncorrectDate() {
        Instant startDate = Instant.now();
        Instant endDate = Instant.now().atZone(ZoneId.systemDefault()).minusMonths(1).toInstant();
        GenericRequest request = baseRequestBuilder()
                .period("periodmonth")
                .startDate(startDate)
                .endDate(endDate)
                .build();

        FavouritesChampsResponse response = UserClient.getFavouritesChamps(request);

        List<FavouritesChampsResponse.Result> events = response.getResult();
        assertTrue(events.isEmpty(), "Список событий должен быть пуст при некорректной дате");
    }
}