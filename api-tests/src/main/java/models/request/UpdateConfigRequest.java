package models.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import models.response.config_settings.HighlightEvent;
import models.response.config_settings.LanguageTab;
import models.response.config_settings.Sport;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateConfigRequest {
    @JsonProperty("configId")
    private int configId;

    @JsonProperty("highlightsEvents")
    private List<HighlightEvent> highlightsEvents;

    @JsonProperty("languageTabs")
    private List<LanguageTab> languageTabs;

    @JsonProperty("sports")
    private List<Sport> sports;
}

