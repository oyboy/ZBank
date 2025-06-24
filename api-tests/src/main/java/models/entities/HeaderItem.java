package models.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HeaderItem {
    @JsonProperty("Id")
    private Integer id;
    @JsonProperty("Name")
    private String name;
}