package org.example.api.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaticTranslationsRequest {
    @JsonProperty("timezoneOffset")
    private final int timezoneOffset = -180;

    @JsonProperty("langId")
    private final int langId = 8;

    @JsonProperty("skinName")
    private final String skinName = "betsonic";

    @JsonProperty("configId")
    private final int configId = 1;

    @JsonProperty("culture")
    @Setter
    private String culture;

    @JsonProperty("countryCode")
    private final String countryCode = null;

    @JsonProperty("deviceType")
    private final String deviceType = "Mobile";

    @JsonProperty("numformat")
    private final String numformat = "en";

    @JsonProperty("integration")
    private final String integration = "skintest";
}
