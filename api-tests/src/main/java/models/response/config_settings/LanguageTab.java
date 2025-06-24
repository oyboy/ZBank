package models.response.config_settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LanguageTab {
    @JsonProperty("LanguageId")
    private Integer languageId;
    @JsonProperty("highlightsEvents")
    private List<HighlightEvent> highlightsEvents = new ArrayList<HighlightEvent>();
}
