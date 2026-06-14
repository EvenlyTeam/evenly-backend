package com.evenly.user.application.port.out;

import java.util.UUID;

public interface TokenProvider {

    String issueToken(UUID userId);

    /** 유효한 토큰이면 사용자 ID 를 반환하고, 아니면 예외를 던진다. */
    UUID parseUserId(String token);
}
