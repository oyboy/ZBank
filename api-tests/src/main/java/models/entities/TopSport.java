package models.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TopSport {
    @JsonProperty("SportId")
    private Integer sportId;
    @JsonProperty("Order")
    private Integer order;
    @JsonProperty("IsEnabled")
    private Boolean isEnabled;
}
