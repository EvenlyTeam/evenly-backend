package com.evenly.user.application.service;

import com.evenly.common.domain.UnauthorizedException;
import com.evenly.user.application.dto.AuthTokens;
import com.evenly.user.application.dto.LoginCommand;
import com.evenly.user.application.port.in.LoginUseCase;
import com.evenly.user.application.port.out.LoadUserPort;
import com.evenly.user.application.port.out.PasswordHasher;
import com.evenly.user.application.port.out.TokenProvider;
import com.evenly.user.domain.Email;
import com.evenly.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
class LoginService implements LoginUseCase {

    private static final String INVALID = "이메일 또는 비밀번호가 올바르지 않습니다";

    private final LoadUserPort loadUserPort;
    private final PasswordHasher passwordHasher;
    private final TokenProvider tokenProvider;

    LoginService(LoadUserPort loadUserPort, PasswordHasher passwordHasher, TokenProvider tokenProvider) {
        this.loadUserPort = loadUserPort;
        this.passwordHasher = passwordHasher;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public AuthTokens login(LoginCommand command) {
        String email = new Email(command.email()).value();
        User user = loadUserPort.findByEmail(email).orElseThrow(() -> new UnauthorizedException(INVALID));
        if (!passwordHasher.matches(command.rawPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException(INVALID);
        }
        return new AuthTokens(
                tokenProvider.issueAccessToken(user.getId()),
                tokenProvider.issueRefreshToken(user.getId()),
                user.getId(),
                user.getEmail().value());
    }
}
