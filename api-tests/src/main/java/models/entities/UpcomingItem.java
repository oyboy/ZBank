package models.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties({"Events"})
public class UpcomingItem {
    @JsonProperty("Id")
    private int id;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Node")
    private String node;
    @JsonProperty("Headers")
    private List<Header> headers;
    @JsonProperty("Order")
    private int order;
    @JsonProperty("SportTypeId")
    private int sportTypeId;
}
