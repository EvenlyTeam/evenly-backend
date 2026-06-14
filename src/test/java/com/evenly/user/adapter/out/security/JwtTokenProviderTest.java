package com.evenly.user.adapter.out.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

    private static final String SECRET = "evenly-test-secret-please-change-me-0123456789";
    private final JwtTokenProvider provider = new JwtTokenProvider(SECRET, 3600000, 1209600000);

    @Test
    void access_토큰_발급_파싱() {
        UUID userId = UUID.randomUUID();
        assertThat(provider.parseAccessUserId(provider.issueAccessToken(userId)))
                .isEqualTo(userId);
    }

    @Test
    void refresh_토큰_발급_파싱() {
        UUID userId = UUID.randomUUID();
        assertThat(provider.parseRefreshUserId(provider.issueRefreshToken(userId)))
                .isEqualTo(userId);
    }

    @Test
    void access_토큰을_refresh로_파싱하면_실패() {
        String access = provider.issueAccessToken(UUID.randomUUID());
        assertThatThrownBy(() -> provider.parseRefreshUserId(access)).isInstanceOf(Exception.class);
    }

    @Test
    void refresh_토큰을_access로_파싱하면_실패() {
        String refresh = provider.issueRefreshToken(UUID.randomUUID());
        assertThatThrownBy(() -> provider.parseAccessUserId(refresh)).isInstanceOf(Exception.class);
    }

    @Test
    void 잘못된_토큰은_실패() {
        assertThatThrownBy(() -> provider.parseAccessUserId("not.a.jwt")).isInstanceOf(Exception.class);
    }

    @Test
    void 다른_시크릿으로_서명된_토큰은_거부() {
        String token = new JwtTokenProvider("another-secret-please-change-me-0123456789", 3600000, 1209600000)
                .issueAccessToken(UUID.randomUUID());
        assertThatThrownBy(() -> provider.parseAccessUserId(token)).isInstanceOf(Exception.class);
    }
}
