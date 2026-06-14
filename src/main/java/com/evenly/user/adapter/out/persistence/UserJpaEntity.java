package com.evenly.user.adapter.out.persistence;

import com.evenly.common.adapter.out.persistence.BaseJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "users", schema = "evenly")
class UserJpaEntity extends BaseJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 256)
    private String email;

    @Column(name = "display_name", nullable = false, length = 50)
    private String displayName;

    @Column(name = "password_hash", nullable = false, length = 256)
    private String passwordHash;

    protected UserJpaEntity() {}

    UserJpaEntity(UUID id, String email, String displayName, String passwordHash) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.passwordHash = passwordHash;
    }

    UUID getId() {
        return id;
    }

    String getEmail() {
        return email;
    }

    String getDisplayName() {
        return displayName;
    }

    String getPasswordHash() {
        return passwordHash;
    }
}
