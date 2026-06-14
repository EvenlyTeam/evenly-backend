package com.evenly.group.adapter.out.persistence;

import com.evenly.group.domain.Participant;

final class ParticipantMapper {

    private ParticipantMapper() {}

    static Participant toDomain(ParticipantJpaEntity entity) {
        return new Participant(entity.getId(), entity.getGroupId(), entity.getUserId(), entity.getName());
    }

    static ParticipantJpaEntity toEntity(Participant participant) {
        return new ParticipantJpaEntity(
                participant.getId(),
                participant.getGroupId(),
                participant.getUserId(),
                participant.getName().value());
    }
}
