package com.evenly.user.application.dto;

import java.util.UUID;

/** 인증 성공 시 발급되는 토큰 묶음. accessToken 은 응답 바디, refreshToken 은 httpOnly 쿠키로 전달된다. */
public record AuthTokens(String accessToken, String refreshToken, UUID userId, String email) {}
