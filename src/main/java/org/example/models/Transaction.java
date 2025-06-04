package org.example.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Getter;
import org.example.models.enums.TransactionType;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
public class Transaction {
    @Builder.Default
    private final String id = UUID.randomUUID().toString();
    private final TransactionType type;
    private final Double amount;
    private final LocalDateTime timestamp;
    private final String sourceAccountId;
    private final String targetAccountId;

    @JsonCreator
    public static Transaction fromJson(
            @JsonProperty("id") String id,
            @JsonProperty("type") TransactionType type,
            @JsonProperty("amount") Double amount,
            @JsonProperty("timestamp") LocalDateTime timestamp,
            @JsonProperty("sourceAccountId") String sourceAccountId,
            @JsonProperty("targetAccountId") String targetAccountId) {
        return Transaction.builder()
                .id(id)
                .type(type)
                .amount(amount)
                .timestamp(timestamp)
                .sourceAccountId(sourceAccountId)
                .targetAccountId(targetAccountId)
                .build();
    }
}