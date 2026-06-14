package com.evenly.group.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class GroupNameTest {

    @Test
    void 유효한_이름을_보관한다() {
        assertThat(new GroupName("강릉 여행 모임").value()).isEqualTo("강릉 여행 모임");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t"})
    void 빈_이름은_거부한다(String blank) {
        assertThatThrownBy(() -> new GroupName(blank)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void null_이름은_거부한다() {
        assertThatThrownBy(() -> new GroupName(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 최대_길이를_초과하면_거부한다() {
        assertThatThrownBy(() -> new GroupName("a".repeat(51))).isInstanceOf(IllegalArgumentException.class);
    }
}
