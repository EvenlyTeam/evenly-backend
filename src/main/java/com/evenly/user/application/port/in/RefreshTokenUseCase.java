package com.evenly.user.application.port.in;

import com.evenly.user.application.dto.AuthTokens;

public interface RefreshTokenUseCase {

    /** refresh 토큰으로 새 access/refresh 토큰을 재발급한다(회전). 유효하지 않으면 인증 예외. */
    AuthTokens refresh(String refreshToken);
}
