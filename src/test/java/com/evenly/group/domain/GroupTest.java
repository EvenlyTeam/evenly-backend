package com.evenly.group.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class GroupTest {

    @Test
    void create_는_id를_발급하고_토큰과_생성시각은_비운다() {
        UUID owner = UUID.randomUUID();

        Group group = Group.create("강릉 여행 모임", owner);

        assertThat(group.getId()).isNotNull();
        assertThat(group.getName().value()).isEqualTo("강릉 여행 모임");
        assertThat(group.getOwnerId()).isEqualTo(owner);
        assertThat(group.getShareToken()).isNull();
        assertThat(group.getCreatedAt()).isNull();
    }

    @Test
    void 소유자가_null이면_거부한다() {
        assertThatThrownBy(() -> Group.create("강릉", null)).isInstanceOf(IllegalArgumentException.class);
    }
}
