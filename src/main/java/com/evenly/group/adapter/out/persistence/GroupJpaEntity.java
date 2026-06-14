package com.evenly.group.adapter.out.persistence;

import com.evenly.common.adapter.out.persistence.BaseJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "groups", schema = "evenly")
class GroupJpaEntity extends BaseJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(name = "share_token", length = 64, unique = true)
    private String shareToken;

    @Column(name = "settled_at")
    private OffsetDateTime settledAt;

    protected GroupJpaEntity() {}

    GroupJpaEntity(UUID id, String name, UUID ownerId, String shareToken, OffsetDateTime settledAt) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.shareToken = shareToken;
        this.settledAt = settledAt;
    }

    UUID getId() {
        return id;
    }

    String getName() {
        return name;
    }

    UUID getOwnerId() {
        return ownerId;
    }

    String getShareToken() {
        return shareToken;
    }

    OffsetDateTime getSettledAt() {
        return settledAt;
    }
}
