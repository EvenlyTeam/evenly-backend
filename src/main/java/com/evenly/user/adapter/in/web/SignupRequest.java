package com.evenly.user.adapter.in.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "회원가입 요청")
public record SignupRequest(
        @Schema(description = "이메일", example = "junho@example.com")
                @NotBlank(message = "이메일은 필수입니다") @Email(message = "이메일 형식이 올바르지 않습니다") String email,
        @Schema(description = "표시 이름(닉네임)", example = "준호")
                @NotBlank(message = "이름은 필수입니다") @Size(max = 50, message = "이름은 최대 50자입니다") String displayName,
        @Schema(description = "비밀번호(8~72자)", example = "password123")
                @NotBlank(message = "비밀번호는 필수입니다") @Size(min = 8, max = 72, message = "비밀번호는 8~72자입니다") String password) {}
