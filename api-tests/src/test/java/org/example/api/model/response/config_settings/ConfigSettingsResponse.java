package org.example.api.model.response.config_settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@lombok.Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigSettingsResponse {
        @JsonProperty("Data")
        private Data data;
}