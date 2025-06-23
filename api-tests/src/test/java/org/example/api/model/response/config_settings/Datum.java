package org.example.api.model.response.config_settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
@Data
public class Datum {
    @JsonProperty("SportId")
    private Integer sportId;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Count")
    private Integer count;
    @JsonProperty("Categories")
    private List<Category> categories;
}
