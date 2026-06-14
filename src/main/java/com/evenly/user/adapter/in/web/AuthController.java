package com.evenly.user.adapter.in.web;

import com.evenly.user.application.dto.AuthResult;
import com.evenly.user.application.dto.AuthTokens;
import com.evenly.user.application.dto.LoginCommand;
import com.evenly.user.application.dto.SignupCommand;
import com.evenly.user.application.port.in.LoginUseCase;
import com.evenly.user.application.port.in.RefreshTokenUseCase;
import com.evenly.user.application.port.in.SignupUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/auth")
class AuthController {

    private static final String ACCESS_COOKIE = "accessToken";
    private static final String REFRESH_COOKIE = "refreshToken";
    private static final String ACCESS_COOKIE_PATH = "/";
    private static final String REFRESH_COOKIE_PATH = "/auth";

    private final SignupUseCase signupUseCase;
    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final boolean cookieSecure;
    private final long accessValidityMs;
    private final long refreshValidityMs;

    AuthController(
            SignupUseCase signupUseCase,
            LoginUseCase loginUseCase,
            RefreshTokenUseCase refreshTokenUseCase,
            @Value("${app.cookie.secure}") boolean cookieSecure,
            @Value("${jwt.access-token-validity-ms}") long accessValidityMs,
            @Value("${jwt.refresh-token-validity-ms}") long refreshValidityMs) {
        this.signupUseCase = signupUseCase;
        this.loginUseCase = loginUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.cookieSecure = cookieSecure;
        this.accessValidityMs = accessValidityMs;
        this.refreshValidityMs = refreshValidityMs;
    }

    @Operation(summary = "회원가입", description = "가입 후 access/refresh 토큰을 httpOnly 쿠키 + access는 바디로도 발급.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "가입 성공"),
        @ApiResponse(responseCode = "409", description = "이미 가입된 이메일")
    })
    @PostMapping("/signup")
    public ResponseEntity<AuthResult> signup(@Valid @RequestBody SignupRequest request) {
        AuthTokens tokens =
                signupUseCase.signup(new SignupCommand(request.email(), request.displayName(), request.password()));
        return tokenResponse(tokens, HttpStatus.CREATED);
    }

    @Operation(summary = "로그인", description = "자격 증명 검증 후 access/refresh 토큰을 httpOnly 쿠키 + access는 바디로도 발급.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그인 성공"),
        @ApiResponse(responseCode = "401", description = "이메일 또는 비밀번호 오류")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResult> login(@Valid @RequestBody LoginRequest request) {
        AuthTokens tokens = loginUseCase.login(new LoginCommand(request.email(), request.password()));
        return tokenResponse(tokens, HttpStatus.OK);
    }

    @Operation(summary = "토큰 재발급", description = "refresh 토큰(쿠키)으로 새 access/refresh 토큰을 발급한다(회전).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "재발급 성공"),
        @ApiResponse(responseCode = "401", description = "refresh 토큰 없음/만료/위조")
    })
    @PostMapping("/refresh")
    public ResponseEntity<AuthResult> refresh(
            @Parameter(hidden = true) @CookieValue(value = REFRESH_COOKIE, required = false) String refreshToken) {
        AuthTokens tokens = refreshTokenUseCase.refresh(refreshToken);
        return tokenResponse(tokens, HttpStatus.OK);
    }

    @Operation(summary = "로그아웃", description = "access/refresh 쿠키를 모두 만료시킨다.")
    @ApiResponse(responseCode = "204", description = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, expire(ACCESS_COOKIE, ACCESS_COOKIE_PATH))
                .header(HttpHeaders.SET_COOKIE, expire(REFRESH_COOKIE, REFRESH_COOKIE_PATH))
                .build();
    }

    private ResponseEntity<AuthResult> tokenResponse(AuthTokens tokens, HttpStatus status) {
        String accessCookie = cookie(ACCESS_COOKIE, tokens.accessToken(), ACCESS_COOKIE_PATH, accessValidityMs);
        String refreshCookie = cookie(REFRESH_COOKIE, tokens.refreshToken(), REFRESH_COOKIE_PATH, refreshValidityMs);
        return ResponseEntity.status(status)
                .header(HttpHeaders.SET_COOKIE, accessCookie)
                .header(HttpHeaders.SET_COOKIE, refreshCookie)
                .body(AuthResult.of(tokens.accessToken(), tokens.userId(), tokens.email()));
    }

    private String cookie(String name, String value, String path, long maxAgeMs) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Strict")
                .path(path)
                .maxAge(Duration.ofMillis(maxAgeMs))
                .build()
                .toString();
    }

    private String expire(String name, String path) {
        return ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Strict")
                .path(path)
                .maxAge(0)
                .build()
                .toString();
    }
}
