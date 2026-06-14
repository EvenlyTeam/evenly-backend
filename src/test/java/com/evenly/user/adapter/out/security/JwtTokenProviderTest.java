package com.evenly.user.adapter.out.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

    private static final String SECRET = "evenly-test-secret-please-change-me-0123456789";
    private final JwtTokenProvider provider = new JwtTokenProvider(SECRET, 3600000);

    @Test
    void 토큰을_발급하고_다시_사용자ID로_파싱한다() {
        UUID userId = UUID.randomUUID();

        String token = provider.issueToken(userId);

        assertThat(provider.parseUserId(token)).isEqualTo(userId);
    }

    @Test
    void 변조되거나_잘못된_토큰은_파싱에_실패한다() {
        assertThatThrownBy(() -> provider.parseUserId("not.a.jwt")).isInstanceOf(Exception.class);
    }

    @Test
    void 다른_시크릿으로_서명된_토큰은_거부한다() {
        String token = new JwtTokenProvider("another-secret-please-change-me-0123456789", 3600000)
                .issueToken(UUID.randomUUID());

        assertThatThrownBy(() -> provider.parseUserId(token)).isInstanceOf(Exception.class);
    }
}
