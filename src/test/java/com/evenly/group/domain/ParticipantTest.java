package com.evenly.group.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ParticipantTest {

    @Test
    void create_는_id를_발급하고_사용자는_연결하지_않는다() {
        UUID groupId = UUID.randomUUID();

        Participant participant = Participant.create(groupId, "준호");

        assertThat(participant.getId()).isNotNull();
        assertThat(participant.getGroupId()).isEqualTo(groupId);
        assertThat(participant.getUserId()).isNull();
        assertThat(participant.getName().value()).isEqualTo("준호");
    }

    @Test
    void belongsTo_는_같은_모임이면_참() {
        UUID groupId = UUID.randomUUID();
        Participant participant = Participant.create(groupId, "준호");

        assertThat(participant.belongsTo(groupId)).isTrue();
        assertThat(participant.belongsTo(UUID.randomUUID())).isFalse();
    }
}
