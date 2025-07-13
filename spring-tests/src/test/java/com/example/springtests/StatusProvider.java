package com.example.springtests;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class StatusProvider {
    public static Stream<Arguments> provideAllStatuses() {
        return Stream.of(
                Arguments.of("active", false),
                Arguments.of("suspended", false),
                Arguments.of("disabled", true),
                Arguments.of("win", true),
                Arguments.of("loss", true),
                Arguments.of("return", true),
                Arguments.of("half_win", true),
                Arguments.of("half_loss", true),
                Arguments.of("cancelled", true)
        );
    }
}
