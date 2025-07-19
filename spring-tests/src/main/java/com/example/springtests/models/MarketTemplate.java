package com.example.springtests.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarketTemplate {
    private String name;
    private long market_type_id;
    private List<Integer> selections_ids;
}
