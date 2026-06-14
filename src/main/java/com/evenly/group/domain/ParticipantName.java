package com.evenly.group.domain;

public record ParticipantName(String value) {

    private static final int MAX_LENGTH = 50;

    public ParticipantName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("participant name must not be blank");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("participant name must be at most " + MAX_LENGTH + " characters");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
