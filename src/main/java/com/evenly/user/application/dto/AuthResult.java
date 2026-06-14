package com.evenly.user.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "인증 결과 (access 토큰)")
public record AuthResult(
        @Schema(description = "JWT access 토큰") String accessToken,
        @Schema(description = "토큰 타입", example = "Bearer") String tokenType,
        @Schema(description = "사용자 ID") UUID userId,
        @Schema(description = "이메일") String email) {

    public static AuthResult of(String accessToken, UUID userId, String email) {
        return new AuthResult(accessToken, "Bearer", userId, email);
    }
}
