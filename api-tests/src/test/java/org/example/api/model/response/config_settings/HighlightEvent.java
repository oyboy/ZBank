package org.example.api.model.response.config_settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HighlightEvent {
    @JsonProperty("EventId")
    private long eventId;
    @JsonProperty("Order")
    private int order;
    @JsonProperty("IsPromo")
    private boolean isPromo;
    @JsonProperty("IsSafe")
    private boolean isSafe;
}
