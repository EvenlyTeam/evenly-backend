package com.evenly.group.adapter.out.persistence;

import com.evenly.common.adapter.out.persistence.BaseJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "participants", schema = "evenly")
class ParticipantJpaEntity extends BaseJpaEntity {

    @Id
    private UUID id;

    @Column(name = "group_id", nullable = false)
    private UUID groupId;

    @Column(name = "user_id")
    private UUID userId;

    @Column(nullable = false, length = 50)
    private String name;

    protected ParticipantJpaEntity() {}

    ParticipantJpaEntity(UUID id, UUID groupId, UUID userId, String name) {
        this.id = id;
        this.groupId = groupId;
        this.userId = userId;
        this.name = name;
    }

    UUID getId() {
        return id;
    }

    UUID getGroupId() {
        return groupId;
    }

    UUID getUserId() {
        return userId;
    }

    String getName() {
        return name;
    }
}
