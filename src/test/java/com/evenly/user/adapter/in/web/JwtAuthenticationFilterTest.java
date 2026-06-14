package com.evenly.user.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.evenly.user.application.port.out.TokenProvider;
import jakarta.servlet.http.Cookie;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

class JwtAuthenticationFilterTest {

    private final TokenProvider tokenProvider = mock(TokenProvider.class);
    private final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(tokenProvider);

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    private UUID currentPrincipal() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth == null ? null : (UUID) auth.getPrincipal();
    }

    @Test
    void Authorization_헤더의_Bearer_토큰으로_인증한다() throws Exception {
        UUID userId = UUID.randomUUID();
        when(tokenProvider.parseAccessUserId("tok")).thenReturn(userId);
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("Authorization", "Bearer tok");

        filter.doFilter(req, new MockHttpServletResponse(), new MockFilterChain());

        assertThat(currentPrincipal()).isEqualTo(userId);
    }

    @Test
    void accessToken_쿠키로도_인증한다() throws Exception {
        UUID userId = UUID.randomUUID();
        when(tokenProvider.parseAccessUserId("cookie-tok")).thenReturn(userId);
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setCookies(new Cookie("accessToken", "cookie-tok"));

        filter.doFilter(req, new MockHttpServletResponse(), new MockFilterChain());

        assertThat(currentPrincipal()).isEqualTo(userId);
    }

    @Test
    void 토큰이_없으면_인증하지_않는다() throws Exception {
        filter.doFilter(new MockHttpServletRequest(), new MockHttpServletResponse(), new MockFilterChain());

        assertThat(currentPrincipal()).isNull();
    }

    @Test
    void 위조_토큰이면_인증하지_않는다() throws Exception {
        when(tokenProvider.parseAccessUserId("bad")).thenThrow(new RuntimeException("invalid"));
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("Authorization", "Bearer bad");

        filter.doFilter(req, new MockHttpServletResponse(), new MockFilterChain());

        assertThat(currentPrincipal()).isNull();
    }
}
