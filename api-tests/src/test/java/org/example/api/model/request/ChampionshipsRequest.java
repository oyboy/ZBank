package org.example.api.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChampionshipsRequest {
    @JsonProperty("SportIds")
    private int[] sportIds;

    @JsonProperty("dateFrom")
    private Instant dateFrom;

    @JsonProperty("dateTo")
    private Instant dateTo;
}