package org.example.api.model.response.config_settings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
@Data
public class GetChampionshipResponse {
    @JsonProperty("Data")
    private List<Sport> sport;
    @JsonProperty("Success")
    private Boolean success;
    @JsonProperty("Error")
    private Object error;
}
