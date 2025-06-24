package models.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class StaticTranslationsResponse {
    @JsonProperty("Result")
    private Map<String, String> result;
}
