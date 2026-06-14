package com.evenly.user.adapter.in.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "내 정보 수정 요청")
public record UpdateProfileRequest(
        @Schema(description = "표시 이름(닉네임)", example = "준호")
                @NotBlank(message = "이름은 필수입니다") @Size(max = 50, message = "이름은 최대 50자입니다") String displayName) {}
