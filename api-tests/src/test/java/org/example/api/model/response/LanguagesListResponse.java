package org.example.api.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class LanguagesListResponse {
    @JsonProperty("Data")
    private List<Language> data;

    @JsonProperty("Success")
    private boolean success;

    @JsonProperty("Error")
    private String error;
}