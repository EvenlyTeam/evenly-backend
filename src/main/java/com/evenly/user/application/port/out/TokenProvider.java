package com.evenly.user.application.port.out;

import java.util.UUID;

public interface TokenProvider {

    String issueAccessToken(UUID userId);

    String issueRefreshToken(UUID userId);

    /** 유효한 access 토큰이면 사용자 ID 반환, 아니면(만료/위조/refresh 토큰) 예외. */
    UUID parseAccessUserId(String token);

    /** 유효한 refresh 토큰이면 사용자 ID 반환, 아니면(만료/위조/access 토큰) 예외. */
    UUID parseRefreshUserId(String token);
}
