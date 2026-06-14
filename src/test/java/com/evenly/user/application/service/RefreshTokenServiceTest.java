package com.evenly.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.evenly.common.domain.UnauthorizedException;
import com.evenly.user.application.dto.AuthTokens;
import com.evenly.user.application.port.out.LoadUserPort;
import com.evenly.user.application.port.out.TokenProvider;
import com.evenly.user.domain.User;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    LoadUserPort loadUserPort;

    @Mock
    TokenProvider tokenProvider;

    @InjectMocks
    RefreshTokenService service;

    @Test
    void 유효한_refresh면_새_토큰을_회전발급한다() {
        UUID uid = UUID.randomUUID();
        when(tokenProvider.parseRefreshUserId("rt")).thenReturn(uid);
        when(loadUserPort.findById(uid)).thenReturn(Optional.of(new User(uid, "a@b.com", "준호", "h", null)));
        when(tokenProvider.issueAccessToken(uid)).thenReturn("new-access");
        when(tokenProvider.issueRefreshToken(uid)).thenReturn("new-refresh");

        AuthTokens result = service.refresh("rt");

        assertThat(result.accessToken()).isEqualTo("new-access");
        assertThat(result.refreshToken()).isEqualTo("new-refresh");
    }

    @Test
    void 토큰이_null이면_Unauthorized() {
        assertThatThrownBy(() -> service.refresh(null)).isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void 위조_토큰이면_Unauthorized() {
        when(tokenProvider.parseRefreshUserId("bad")).thenThrow(new RuntimeException("invalid"));

        assertThatThrownBy(() -> service.refresh("bad")).isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void 사용자가_없으면_Unauthorized() {
        UUID uid = UUID.randomUUID();
        when(tokenProvider.parseRefreshUserId("rt")).thenReturn(uid);
        when(loadUserPort.findById(uid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.refresh("rt")).isInstanceOf(UnauthorizedException.class);
    }
}
