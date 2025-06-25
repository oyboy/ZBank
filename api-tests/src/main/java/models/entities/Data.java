package models.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@lombok.Data
public class Data {
    @JsonProperty("Sports")
    private List<Sport> sports;
    @JsonProperty("Events")
    private List<Event> events;
    @JsonProperty("LanguageTabs")
    private List<LanguageTab> languageTabs;
}
