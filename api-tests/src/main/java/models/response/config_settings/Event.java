package models.response.config_settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Event {
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
