package com.evenly.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.evenly.common.domain.ConflictException;
import com.evenly.user.application.dto.AuthTokens;
import com.evenly.user.application.dto.SignupCommand;
import com.evenly.user.application.port.out.LoadUserPort;
import com.evenly.user.application.port.out.PasswordHasher;
import com.evenly.user.application.port.out.SaveUserPort;
import com.evenly.user.application.port.out.TokenProvider;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SignupServiceTest {

    @Mock
    LoadUserPort loadUserPort;

    @Mock
    SaveUserPort saveUserPort;

    @Mock
    PasswordHasher passwordHasher;

    @Mock
    TokenProvider tokenProvider;

    @InjectMocks
    SignupService service;

    @Test
    void 가입하면_비번을_해시하고_토큰을_발급한다() {
        when(loadUserPort.existsByEmail("junho@example.com")).thenReturn(false);
        when(passwordHasher.hash("password123")).thenReturn("hashed");
        when(saveUserPort.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(tokenProvider.issueAccessToken(any())).thenReturn("access-tok");
        when(tokenProvider.issueRefreshToken(any())).thenReturn("refresh-tok");

        AuthTokens result = service.signup(new SignupCommand("Junho@Example.com", "준호", "password123"));

        assertThat(result.accessToken()).isEqualTo("access-tok");
        assertThat(result.refreshToken()).isEqualTo("refresh-tok");
        assertThat(result.email()).isEqualTo("junho@example.com");
        verify(saveUserPort).save(any());
    }

    @Test
    void 이미_가입된_이메일이면_충돌() {
        when(loadUserPort.existsByEmail("junho@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service.signup(new SignupCommand("junho@example.com", "준호", "password123")))
                .isInstanceOf(ConflictException.class);
        verify(saveUserPort, never()).save(any());
        verify(tokenProvider, never()).issueAccessToken(any(UUID.class));
    }
}
