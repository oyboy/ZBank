package org.example.api.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Language {
    @JsonProperty("LanguageId")
    private int languageId;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Code")
    private String code;
}
