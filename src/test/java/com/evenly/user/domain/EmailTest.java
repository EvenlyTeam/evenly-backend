package com.evenly.user.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class EmailTest {

    @Test
    void 공백제거하고_소문자로_정규화한다() {
        assertThat(new Email("  Junho@Example.COM ").value()).isEqualTo("junho@example.com");
    }

    @ParameterizedTest
    @ValueSource(strings = {"no-at-sign", "a@b", "a@b.", "@b.com", "a @b.com"})
    void 형식이_틀리면_거부한다(String invalid) {
        assertThatThrownBy(() -> new Email(invalid)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 빈_값은_거부한다() {
        assertThatThrownBy(() -> new Email(" ")).isInstanceOf(IllegalArgumentException.class);
    }
}
