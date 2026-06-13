package com.evenly.group.domain;

public record GroupName(String value) {

    private static final int MAX_LENGTH = 50;

    public GroupName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("group name must not be blank");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("group name must be at most " + MAX_LENGTH + " characters");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
