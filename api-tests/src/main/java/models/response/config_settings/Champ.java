package models.response.config_settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Champ {
    @JsonProperty("ChampId")
    private int champId;

    @JsonProperty("Name")
    private String name;
}
