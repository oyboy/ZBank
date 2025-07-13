package com.example.springtests.models;

import lombok.Data;

@Data
public class MarketDataRecord {
    private Long id;
    private String eventId;
    private Long marketTypeId;
    private Long selectionTypeId;
    private Double price;
    private Double probability;
    private String status;
}
