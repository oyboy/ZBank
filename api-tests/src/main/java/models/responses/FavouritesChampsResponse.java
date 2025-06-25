package models.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class FavouritesChampsResponse {
    @JsonProperty("Result")
    private List<Result> result;

    @Data
    public static class Result {
        @JsonProperty("SportId")
        private Integer sportId;
        @JsonProperty("SportTypeId")
        private Integer sportTypeId;
        @JsonProperty("SportName")
        private String sportName;
        @JsonProperty("CatId")
        private Integer catId;
        @JsonProperty("CatName")
        private String catName;
        @JsonProperty("ChampName")
        private String champName;
        @JsonProperty("ChampId")
        private Integer champId;
        @JsonProperty("HasLiveEvents")
        private Boolean hasLiveEvents;
        @JsonProperty("EventsCount")
        private Integer eventsCount;
        @JsonProperty("ISO")
        private String iso;
        @JsonProperty("SortOrderBySport")
        private Integer sortOrderBySport;
        @JsonProperty("SortOrderByCategory")
        private Integer sortOrderByCategory;
        @JsonProperty("SortOrderByChamp")
        private Integer sortOrderByChamp;
        @JsonProperty("ChampionshipIds")
        private String championshipIds;
    }
}
