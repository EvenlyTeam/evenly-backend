package com.evenly.group.adapter.out.persistence;

import com.evenly.common.adapter.out.persistence.BaseJpaEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "expenses", schema = "evenly")
class ExpenseJpaEntity extends BaseJpaEntity {

    @Id
    private UUID id;

    @Column(name = "group_id", nullable = false)
    private UUID groupId;

    @Column(name = "payer_id", nullable = false)
    private UUID payerId;

    @Column(nullable = false, length = 100)
    private String description;

    @Column(nullable = false)
    private long amount;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "expense_shares", schema = "evenly", joinColumns = @JoinColumn(name = "expense_id"))
    @Column(name = "participant_id", nullable = false)
    @OrderColumn(name = "share_order")
    private List<UUID> shareParticipantIds = new ArrayList<>();

    protected ExpenseJpaEntity() {}

    ExpenseJpaEntity(
            UUID id, UUID groupId, UUID payerId, String description, long amount, List<UUID> shareParticipantIds) {
        this.id = id;
        this.groupId = groupId;
        this.payerId = payerId;
        this.description = description;
        this.amount = amount;
        this.shareParticipantIds = new ArrayList<>(shareParticipantIds);
    }

    UUID getId() {
        return id;
    }

    UUID getGroupId() {
        return groupId;
    }

    UUID getPayerId() {
        return payerId;
    }

    String getDescription() {
        return description;
    }

    long getAmount() {
        return amount;
    }

    List<UUID> getShareParticipantIds() {
        return shareParticipantIds;
    }
}
