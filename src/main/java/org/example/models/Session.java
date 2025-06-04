package org.example.models;

import lombok.Getter;
import lombok.Setter;

public class Session {
    @Getter
    @Setter
    private static User currentUser;

    public static boolean isAuthenticated() {
        return currentUser != null;
    }

    public static void logout() {
        currentUser = null;
    }
}
