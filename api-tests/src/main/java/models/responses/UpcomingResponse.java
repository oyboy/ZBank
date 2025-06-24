package models.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import models.entities.UpcomingItem;

import java.util.List;

@Data
public class UpcomingResponse {
    @JsonProperty("Result")
    private Result result;

    @Data
    public static class Result {
        @JsonProperty("Items")
        private List<UpcomingItem> items;

        @JsonProperty("EventsCount")
        private int eventsCount;

        @JsonProperty("ShowMoreEventsInt")
        private boolean showMoreEventsInt;

        @JsonProperty("ShowMoreEvents")
        private boolean showMoreEvents;

        @JsonProperty("IsLiveStream")
        private boolean isLiveStream;
    }
}
