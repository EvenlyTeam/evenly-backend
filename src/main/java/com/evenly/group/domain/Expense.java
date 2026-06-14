package com.evenly.group.domain;

import java.util.List;
import java.util.UUID;

public class Expense {

    private static final int MAX_DESCRIPTION_LENGTH = 100;

    private final UUID id;
    private final UUID groupId;
    private final UUID payerId;
    private final String description;
    private final long amount;
    private final List<UUID> shareParticipantIds; // 순서 보존 — 균등분할 나머지는 앞 분담자부터 배분

    public Expense(
            UUID id, UUID groupId, UUID payerId, String description, long amount, List<UUID> shareParticipantIds) {
        if (id == null) {
            throw new IllegalArgumentException("expense id must not be null");
        }
        if (groupId == null) {
            throw new IllegalArgumentException("group id must not be null");
        }
        if (payerId == null) {
            throw new IllegalArgumentException("payer id must not be null");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("description must not be blank");
        }
        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException("description must be at most " + MAX_DESCRIPTION_LENGTH + " characters");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        if (shareParticipantIds == null || shareParticipantIds.isEmpty()) {
            throw new IllegalArgumentException("share participants must not be empty");
        }
        this.id = id;
        this.groupId = groupId;
        this.payerId = payerId;
        this.description = description;
        this.amount = amount;
        this.shareParticipantIds = List.copyOf(shareParticipantIds);
    }

    /** 새 지출 생성. id 는 도메인이 발급한다. */
    public static Expense create(
            UUID groupId, UUID payerId, String description, long amount, List<UUID> shareParticipantIds) {
        return new Expense(UUID.randomUUID(), groupId, payerId, description, amount, shareParticipantIds);
    }

    public boolean belongsTo(UUID groupId) {
        return this.groupId.equals(groupId);
    }

    public UUID getId() {
        return id;
    }

    public UUID getGroupId() {
        return groupId;
    }

    public UUID getPayerId() {
        return payerId;
    }

    public String getDescription() {
        return description;
    }

    public long getAmount() {
        return amount;
    }

    public List<UUID> getShareParticipantIds() {
        return shareParticipantIds;
    }
}
