package org.example.api.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenericRequest {
    @JsonProperty("timezoneOffset")
    private Integer timezoneOffset;

    @JsonProperty("langId")
    private Integer langId;

    @JsonProperty("skinName")
    private String skinName;

    @JsonProperty("configId")
    private Integer configId;

    @JsonProperty("culture")
    private String culture;

    @JsonProperty("countryCode")
    private String countryCode;

    @JsonProperty("deviceType")
    private String deviceType;

    @JsonProperty("numformat")
    private String numformat;

    @JsonProperty("integration")
    private String integration;

    @JsonProperty("sportId")
    private Integer sportId;

    @JsonProperty("showAllEvents")
    private Boolean showAllEvents;

    @JsonProperty("count")
    private Integer count;

    @JsonProperty("hasStreaming")
    private Boolean hasStreaming;

    @JsonProperty("period")
    private String period;

    @JsonProperty("startDate")
    private Instant startDate;

    @JsonProperty("endDate")
    private Instant endDate;
}
