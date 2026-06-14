package com.evenly.user.application.dto;

import com.evenly.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "사용자 정보")
public record UserInfo(
        @Schema(description = "사용자 ID") UUID id,
        @Schema(description = "이메일", example = "junho@example.com") String email,
        @Schema(description = "표시 이름", example = "준호") String displayName) {

    public static UserInfo from(User user) {
        return new UserInfo(user.getId(), user.getEmail().value(), user.getDisplayName());
    }
}
