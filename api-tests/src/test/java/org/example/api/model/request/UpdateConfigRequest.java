package org.example.api.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.example.api.model.response.config_settings.HighlightEvent;
import org.example.api.model.response.config_settings.LanguageTab;
import org.example.api.model.response.config_settings.Sport;

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

