package org.example.models;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class User {
    private final String id;
    private String name;
    private String email;
    private String password;
    private List<String> accountIds;

    public User(){
        this.id = UUID.randomUUID().toString();
    }

    public User(String name, String email, String password) {
        id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
        this.password = password;
        accountIds = new ArrayList<>();
    }
}