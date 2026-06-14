package com.evenly.user.adapter.out.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PasswordHasherAdapterTest {

    private final PasswordHasherAdapter hasher = new PasswordHasherAdapter();

    @Test
    void 해시는_원문과_다르고_검증에_성공한다() {
        String hash = hasher.hash("password123");

        assertThat(hash).isNotEqualTo("password123");
        assertThat(hasher.matches("password123", hash)).isTrue();
    }

    @Test
    void 틀린_비밀번호는_검증에_실패한다() {
        String hash = hasher.hash("password123");

        assertThat(hasher.matches("wrong", hash)).isFalse();
    }
}
