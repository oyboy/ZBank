package models.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import models.entities.HighlightEvent;
import models.entities.LanguageTab;
import models.entities.Sport;

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

