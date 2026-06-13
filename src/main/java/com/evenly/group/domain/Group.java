package com.evenly.group.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Group {

    private final UUID id;
    private final GroupName name;
    private final UUID ownerId;
    private final String shareToken;
    private final OffsetDateTime createdAt;

    public Group(UUID id, String name, UUID ownerId, String shareToken, OffsetDateTime createdAt) {
        if (id == null) {
            throw new IllegalArgumentException("group id must not be null");
        }
        if (ownerId == null) {
            throw new IllegalArgumentException("owner id must not be null");
        }
        this.id = id;
        this.name = new GroupName(name);
        this.ownerId = ownerId;
        this.shareToken = shareToken;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public GroupName getName() {
        return name;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public String getShareToken() {
        return shareToken;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
