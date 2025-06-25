package models.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UpdateConfigResponse {
    @JsonProperty("Success")
    private Boolean success;
    @JsonProperty("Error")
    private Object error;
}
