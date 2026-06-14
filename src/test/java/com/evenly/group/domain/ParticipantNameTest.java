package com.evenly.group.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ParticipantNameTest {

    @Test
    void 유효한_이름을_보관한다() {
        assertThat(new ParticipantName("준호").value()).isEqualTo("준호");
    }

    @Test
    void 빈_이름은_거부한다() {
        assertThatThrownBy(() -> new ParticipantName(" ")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 최대_길이를_초과하면_거부한다() {
        assertThatThrownBy(() -> new ParticipantName("a".repeat(51))).isInstanceOf(IllegalArgumentException.class);
    }
}
