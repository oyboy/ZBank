package models.requests;

public class GenericRequestBuilderProvider {
    public static GenericRequest.GenericRequestBuilder baseRequestBuilder() {
        return GenericRequest.builder()
                .timezoneOffset(420)
                .langId(39)
                .skinName("betsonic")
                .configId(1)
                .culture("fr-fr")
                .countryCode("RU")
                .deviceType("Desktop")
                .numformat("en")
                .integration("skintest")
                .showAllEvents(false)
                .count(10)
                .hasStreaming(false);
    }
}
