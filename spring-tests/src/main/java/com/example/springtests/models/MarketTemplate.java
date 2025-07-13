package com.example.springtests.models;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class MarketTemplate {
    private final String name;
    private final long marketTypeId;
    private final List<Integer> selectionIds;
}
