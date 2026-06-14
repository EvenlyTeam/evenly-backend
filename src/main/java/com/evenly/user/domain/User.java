package com.evenly.user.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

public class User {

    private final UUID id;
    private final Email email;
    private final String passwordHash;
    private final OffsetDateTime createdAt;

    public User(UUID id, String email, String passwordHash, OffsetDateTime createdAt) {
        if (id == null) {
            throw new IllegalArgumentException("user id must not be null");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("password hash must not be blank");
        }
        this.id = id;
        this.email = new Email(email);
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    /** 신규 가입. id 는 도메인이 발급, createdAt 은 영속 시점에 채워진다. */
    public static User register(String email, String passwordHash) {
        return new User(UUID.randomUUID(), email, passwordHash, null);
    }

    public UUID getId() {
        return id;
    }

    public Email getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
