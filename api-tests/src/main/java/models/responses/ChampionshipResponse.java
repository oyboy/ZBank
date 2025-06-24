package models.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import models.entities.Sport;

import java.util.List;
@Data
public class ChampionshipResponse {
    @JsonProperty("Data")
    private List<Sport> sport;
    @JsonProperty("Success")
    private Boolean success;
    @JsonProperty("Error")
    private Object error;
}
