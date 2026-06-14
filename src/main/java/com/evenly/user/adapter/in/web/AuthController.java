package com.evenly.user.adapter.in.web;

import com.evenly.user.application.dto.AuthResult;
import com.evenly.user.application.dto.LoginCommand;
import com.evenly.user.application.dto.SignupCommand;
import com.evenly.user.application.port.in.LoginUseCase;
import com.evenly.user.application.port.in.SignupUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/auth")
class AuthController {

    private final SignupUseCase signupUseCase;
    private final LoginUseCase loginUseCase;

    AuthController(SignupUseCase signupUseCase, LoginUseCase loginUseCase) {
        this.signupUseCase = signupUseCase;
        this.loginUseCase = loginUseCase;
    }

    @Operation(summary = "회원가입", description = "이메일/비밀번호로 가입하고 access 토큰을 발급한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "가입 성공"),
        @ApiResponse(responseCode = "409", description = "이미 가입된 이메일")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    public AuthResult signup(@Valid @RequestBody SignupRequest request) {
        return signupUseCase.signup(new SignupCommand(request.email(), request.password()));
    }

    @Operation(summary = "로그인", description = "자격 증명 검증 후 access 토큰을 발급한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그인 성공"),
        @ApiResponse(responseCode = "401", description = "이메일 또는 비밀번호 오류")
    })
    @PostMapping("/login")
    public AuthResult login(@Valid @RequestBody LoginRequest request) {
        return loginUseCase.login(new LoginCommand(request.email(), request.password()));
    }
}
