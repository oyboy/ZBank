package org.example.models;

import lombok.Getter;
import lombok.Setter;
import org.example.models.enums.AccountType;

import java.util.UUID;
@Getter
@Setter
public class Account {
    private final String id;
    private Double balance;
    private AccountType accountType;

    public Account(){
        this.id = UUID.randomUUID().toString();
    }

    public Account(AccountType accountType) {
        this.id = UUID.randomUUID().toString();
        this.accountType = accountType;
        balance = 0.0;
    }
}