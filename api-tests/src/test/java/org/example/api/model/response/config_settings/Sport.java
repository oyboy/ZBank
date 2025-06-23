package org.example.api.model.response.config_settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Sport {
    @JsonProperty("SportId")
    private Integer sportId;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Order")
    private int order;
    @JsonProperty("IsEnabled")
    private boolean isEnabled;
    @JsonProperty("Count")
    private int count;
    @JsonProperty("Categories")
    private List<Category> categories;
}
