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
    @DisplayName("Check language removal")
    @Description("Removes the language tab from the configuration using the UpdateConfig POST request")
    @Story("Admin_Language Removal")
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
        assertTrue(updateConfigResponse.getSuccess(), "Configuration should be updated successfully");
    }

    @Test
    @DisplayName("Check championships retrieval with correct date")
    @Description("Sends a POST request to /GetChampionships with a valid date range and checks that the Success field returned is true")
    @Story("Admin_Championships")
    @TmsLink("TC_ADMIN_02")
    public void getChampionshipsWithCorrectDate() {
        DateTime startDate = DateTime.now();
        DateTime endDate = DateTime.now().plusDays(1);

        var request = new com.altenar.sb2.admin.model.GetHighlightsChampionshipsRequest();
        request.setSportIds(Arrays.asList(74, 76));
        request.setDateFrom(startDate);
        request.setDateTo(endDate);

        var response = AdminClient.getChampionships(request, cookie);
        assertTrue(response.getSuccess(), "Request should return a list of events with a valid date range");
    }

    @Test
    @DisplayName("Check error when requesting championships with an incorrect date")
    @Description("When passing incorrect dates, the request returns an error")
    @Story("Admin_Championships")
    @TmsLink("TC_ADMIN_03")
    public void getChampionshipsWithIncorrectDate() {
        DateTime startDate = DateTime.now();
        DateTime endDate = DateTime.now().minusDays(1);

        var request = new com.altenar.sb2.admin.model.GetHighlightsChampionshipsRequest();
        request.setSportIds(Arrays.asList(74, 76));
        request.setDateFrom(startDate);
        request.setDateTo(endDate);

        var response = AdminClient.getChampionships(request, cookie);
        assertFalse(response.getSuccess(), "Request should return an error for an invalid date range");
    }
}
