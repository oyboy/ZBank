package org.example.api.model.response.config_settings;

import com.fasterxml.jackson.annotation.JsonProperty;
public class TopEvent {
    @JsonProperty("EventId")
    private Integer eventId;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("EventDate")
    private String eventDate;
    @JsonProperty("SportId")
    private Integer sportId;
    @JsonProperty("Sport")
    private String sport;
    @JsonProperty("Category")
    private String category;
    @JsonProperty("Championship")
    private String championship;
    @JsonProperty("Order")
    private Integer order;
    @JsonProperty("IsPromo")
    private Boolean isPromo;
    @JsonProperty("IsSafe")
    private Boolean isSafe;
}
