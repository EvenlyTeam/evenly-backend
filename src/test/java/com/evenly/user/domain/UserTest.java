package com.evenly.user.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    void register_는_id를_발급하고_이메일을_정규화한다() {
        User user = User.register("Junho@Example.com", "준호", "hashed-pw");

        assertThat(user.getId()).isNotNull();
        assertThat(user.getEmail().value()).isEqualTo("junho@example.com");
        assertThat(user.getDisplayName()).isEqualTo("준호");
        assertThat(user.getPasswordHash()).isEqualTo("hashed-pw");
    }

    @Test
    void 표시이름이_비면_거부한다() {
        assertThatThrownBy(() -> User.register("a@b.com", " ", "hash")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 비밀번호_해시가_비면_거부한다() {
        assertThatThrownBy(() -> User.register("a@b.com", "준호", " ")).isInstanceOf(IllegalArgumentException.class);
    }
}
