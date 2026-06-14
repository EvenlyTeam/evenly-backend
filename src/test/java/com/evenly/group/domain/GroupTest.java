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

    @Test
    void withShareToken_은_토큰을_부여한_새_인스턴스를_반환한다() {
        Group group = Group.create("강릉", UUID.randomUUID());

        Group shared = group.withShareToken("tok123");

        assertThat(group.hasShareToken()).isFalse();
        assertThat(shared.hasShareToken()).isTrue();
        assertThat(shared.getShareToken()).isEqualTo("tok123");
        assertThat(shared.getId()).isEqualTo(group.getId());
    }

    @Test
    void withShareToken_은_빈_토큰을_거부한다() {
        Group group = Group.create("강릉", UUID.randomUUID());

        assertThatThrownBy(() -> group.withShareToken(" ")).isInstanceOf(IllegalArgumentException.class);
    }
}
