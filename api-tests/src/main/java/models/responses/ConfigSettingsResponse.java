package models.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import models.entities.Data;

@lombok.Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigSettingsResponse {
        @JsonProperty("Data")
        private Data data;
}