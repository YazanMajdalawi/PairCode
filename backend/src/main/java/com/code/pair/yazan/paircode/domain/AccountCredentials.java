package com.code.pair.yazan.paircode.domain;

public record AccountCredentials(String username, String password) {
    public AccountCredentials {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Username and password cannot be null");
        }

        // more validation...
    }
}