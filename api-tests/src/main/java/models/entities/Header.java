package models.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Header {
    @JsonProperty("Items")
    private List<HeaderItem> items;
    @JsonProperty("SortOrder")
    private Integer sortOrder;
    @JsonProperty("Template")
    private Integer template;
    @JsonProperty("ColumnCount")
    private Integer columnCount;
    @JsonProperty("MColumnCount")
    private Integer mColumnCount;
    @JsonProperty("MarketTypeId")
    private String marketTypeId;
    @JsonProperty("Name")
    private String name;
}
