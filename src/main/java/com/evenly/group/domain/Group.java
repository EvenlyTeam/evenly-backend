package com.evenly.group.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Group {

    private final UUID id;
    private final GroupName name;
    private final UUID ownerId;
    private final String shareToken;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime settledAt; // null=진행중, 값=정산 완료(사용자 선언)

    public Group(UUID id, String name, UUID ownerId, String shareToken, OffsetDateTime createdAt) {
        this(id, name, ownerId, shareToken, createdAt, null);
    }

    public Group(
            UUID id, String name, UUID ownerId, String shareToken, OffsetDateTime createdAt, OffsetDateTime settledAt) {
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
        this.settledAt = settledAt;
    }

    /** 새 모임 생성. id 는 도메인이 발급하고, createdAt 은 영속 시점에 채워진다. */
    public static Group create(String name, UUID ownerId) {
        return new Group(UUID.randomUUID(), name, ownerId, null, null, null);
    }

    /** 이름을 바꾼 새 인스턴스. */
    public Group rename(String newName) {
        return new Group(id, newName, ownerId, shareToken, createdAt, settledAt);
    }

    /** 공유 토큰을 부여한 새 인스턴스. (불변 도메인이라 새 객체로 반환) */
    public Group withShareToken(String shareToken) {
        if (shareToken == null || shareToken.isBlank()) {
            throw new IllegalArgumentException("share token must not be blank");
        }
        return new Group(id, name.value(), ownerId, shareToken, createdAt, settledAt);
    }

    /** 정산 완료로 표시(실제 송금 완료를 사용자가 선언). */
    public Group markSettled() {
        return new Group(id, name.value(), ownerId, shareToken, createdAt, OffsetDateTime.now());
    }

    /** 정산 완료 해제(되돌리기). */
    public Group reopen() {
        return new Group(id, name.value(), ownerId, shareToken, createdAt, null);
    }

    public boolean hasShareToken() {
        return shareToken != null;
    }

    public boolean isOwnedBy(UUID userId) {
        return ownerId.equals(userId);
    }

    public boolean isSettled() {
        return settledAt != null;
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

    public OffsetDateTime getSettledAt() {
        return settledAt;
    }
}
