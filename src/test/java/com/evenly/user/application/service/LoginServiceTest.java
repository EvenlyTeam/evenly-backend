package com.evenly.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.evenly.common.domain.UnauthorizedException;
import com.evenly.user.application.dto.AuthResult;
import com.evenly.user.application.dto.LoginCommand;
import com.evenly.user.application.port.out.LoadUserPort;
import com.evenly.user.application.port.out.PasswordHasher;
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
class LoginServiceTest {

    @Mock
    LoadUserPort loadUserPort;

    @Mock
    PasswordHasher passwordHasher;

    @Mock
    TokenProvider tokenProvider;

    @InjectMocks
    LoginService service;

    @Test
    void 자격증명이_맞으면_토큰을_발급한다() {
        User user = new User(UUID.randomUUID(), "junho@example.com", "준호", "hashed", null);
        when(loadUserPort.findByEmail("junho@example.com")).thenReturn(Optional.of(user));
        when(passwordHasher.matches("password123", "hashed")).thenReturn(true);
        when(tokenProvider.issueToken(user.getId())).thenReturn("jwt-token");

        AuthResult result = service.login(new LoginCommand("junho@example.com", "password123"));

        assertThat(result.accessToken()).isEqualTo("jwt-token");
    }

    @Test
    void 없는_이메일이면_Unauthorized() {
        when(loadUserPort.findByEmail("none@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.login(new LoginCommand("none@example.com", "password123")))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void 비밀번호가_틀리면_Unauthorized() {
        User user = new User(UUID.randomUUID(), "junho@example.com", "준호", "hashed", null);
        when(loadUserPort.findByEmail("junho@example.com")).thenReturn(Optional.of(user));
        when(passwordHasher.matches(any(), any())).thenReturn(false);

        assertThatThrownBy(() -> service.login(new LoginCommand("junho@example.com", "wrong")))
                .isInstanceOf(UnauthorizedException.class);
    }
}
