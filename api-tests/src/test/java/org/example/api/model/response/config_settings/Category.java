package org.example.api.model.response.config_settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Category {
    @JsonProperty("CategoryId")
    private int categoryId;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Champs")
    private List<Champ> champs;
}
