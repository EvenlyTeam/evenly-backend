package com.evenly.user.application.service;

import com.evenly.common.domain.UnauthorizedException;
import com.evenly.user.application.dto.AuthTokens;
import com.evenly.user.application.port.in.RefreshTokenUseCase;
import com.evenly.user.application.port.out.LoadUserPort;
import com.evenly.user.application.port.out.TokenProvider;
import com.evenly.user.domain.User;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
class RefreshTokenService implements RefreshTokenUseCase {

    private static final String INVALID = "유효하지 않은 refresh 토큰입니다";

    private final LoadUserPort loadUserPort;
    private final TokenProvider tokenProvider;

    RefreshTokenService(LoadUserPort loadUserPort, TokenProvider tokenProvider) {
        this.loadUserPort = loadUserPort;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public AuthTokens refresh(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new UnauthorizedException(INVALID);
        }
        UUID userId;
        try {
            userId = tokenProvider.parseRefreshUserId(refreshToken);
        } catch (Exception e) {
            throw new UnauthorizedException(INVALID);
        }
        // 사용자가 삭제되었을 수 있으니 존재 확인 + 이메일 조회.
        User user = loadUserPort.findById(userId).orElseThrow(() -> new UnauthorizedException(INVALID));
        return new AuthTokens(
                tokenProvider.issueAccessToken(user.getId()),
                tokenProvider.issueRefreshToken(user.getId()), // 회전
                user.getId(),
                user.getEmail().value());
    }
}
