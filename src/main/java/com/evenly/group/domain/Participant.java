package com.evenly.group.domain;

import java.util.UUID;

public class Participant {

    private final UUID id;
    private final UUID groupId;
    private final UUID userId; // nullable — 로그인 사용자와의 soft link("내 잔액"용)
    private final ParticipantName name;

    public Participant(UUID id, UUID groupId, UUID userId, String name) {
        if (id == null) {
            throw new IllegalArgumentException("participant id must not be null");
        }
        if (groupId == null) {
            throw new IllegalArgumentException("group id must not be null");
        }
        this.id = id;
        this.groupId = groupId;
        this.userId = userId;
        this.name = new ParticipantName(name);
    }

    /** 이름만으로 모임에 참여자 추가 (가입 회원이 아니어도 됨). */
    public static Participant create(UUID groupId, String name) {
        return new Participant(UUID.randomUUID(), groupId, null, name);
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

    public UUID getUserId() {
        return userId;
    }

    public ParticipantName getName() {
        return name;
    }
}
