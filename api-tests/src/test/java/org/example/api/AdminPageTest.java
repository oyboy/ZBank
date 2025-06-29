package org.example.api;

import clients.AdminClient;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import org.joda.time.DateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class AdminPageTest extends BaseTest {
    @Test
    @DisplayName("Проверка удаления языка")
    @Description("Удаляет языковую вкладку из конфигурации при помощи POST-запроса UpdateConfig")
    @Story("Admin_Удаление языка")
    @TmsLink("TC_ADMIN_01")
    public void testRemoveLanguage() {
        String languageToRemove = "French";
        com.altenar.sb2.admin.model.HighlightsConfigSettingsApiResult configSettings = AdminClient.getConfigSettings(cookie);

        com.altenar.sb2.admin.model.UpdateHighlightsConfigRequest request = new com.altenar.sb2.admin.model.UpdateHighlightsConfigRequest();
        request.setConfigId(126);
        request.setHighlightsEvents(AdminClient.getHighlightEvents(cookie));
        request.setLanguageTabs(AdminClient.removeLanguageByName(configSettings.getData().getLanguageTabs(), languageToRemove));
        request.setSports(AdminClient.getSportsRequestItem(configSettings.getData().getSports()));

        com.altenar.sb2.admin.model.ApiResult updateConfigResponse = AdminClient.updateConfig(request, cookie);
        assertTrue(updateConfigResponse.getSuccess(), "Конфигурация должна быть успешно обновлена");
    }


    @Test
    @DisplayName("Проверка получения чемпионатов при корректной дате")
    @Description("Отправляет POST-запрос на /GetChampionships с валидным диапазоном дат и проверяет, что возвращаемое поле Success = true")
    @Story("Admin_Чемпионаты")
    @TmsLink("TC_ADMIN_02")
    public void getChampionshipsWithCorrectDate() {
        DateTime startDate = DateTime.now();
        DateTime endDate = DateTime.now().plusDays(1);

        var request = new com.altenar.sb2.admin.model.GetHighlightsChampionshipsRequest();
        request.setSportIds(Arrays.asList(74, 76));
        request.setDateFrom(startDate);
        request.setDateTo(endDate);

        var response = AdminClient.getChampionships(request, cookie);
        assertTrue(response.getSuccess(), "Запрос должен вернуть список событий при корректном диапазоне дат");
    }

    @Test
    @DisplayName("Проверка ошибки при запросе чемпионатов с некорректной датой")
    @Description("При передаче некорректных дат запрос возвращает ошибку")
    @Story("Admin_Чемпионаты")
    @TmsLink("TC_ADMIN_03")
    public void getChampionshipsWithIncorrectDate() {
        DateTime startDate = DateTime.now();
        DateTime endDate = DateTime.now().minusDays(1);

        var request = new com.altenar.sb2.admin.model.GetHighlightsChampionshipsRequest();
        request.setSportIds(Arrays.asList(74, 76));
        request.setDateFrom(startDate);
        request.setDateTo(endDate);

        var response = AdminClient.getChampionships(request, cookie);
        assertFalse(response.getSuccess(), "Запрос должен вернуть ошибку при некорректном диапазоне дат");
    }
}
