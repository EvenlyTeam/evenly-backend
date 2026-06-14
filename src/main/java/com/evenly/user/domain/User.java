package com.evenly.user.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

public class User {

    private static final int MAX_DISPLAY_NAME_LENGTH = 50;

    private final UUID id;
    private final Email email;
    private final String displayName;
    private final String passwordHash;
    private final OffsetDateTime createdAt;

    public User(UUID id, String email, String displayName, String passwordHash, OffsetDateTime createdAt) {
        if (id == null) {
            throw new IllegalArgumentException("user id must not be null");
        }
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("display name must not be blank");
        }
        if (displayName.length() > MAX_DISPLAY_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    "display name must be at most " + MAX_DISPLAY_NAME_LENGTH + " characters");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("password hash must not be blank");
        }
        this.id = id;
        this.email = new Email(email);
        this.displayName = displayName;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    /** 신규 가입. id 는 도메인이 발급, createdAt 은 영속 시점에 채워진다. */
    public static User register(String email, String displayName, String passwordHash) {
        return new User(UUID.randomUUID(), email, displayName, passwordHash, null);
    }

    /** 표시 이름(닉네임)을 바꾼 새 인스턴스. */
    public User withDisplayName(String newDisplayName) {
        return new User(id, email.value(), newDisplayName, passwordHash, createdAt);
    }

    public UUID getId() {
        return id;
    }

    public Email getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
