package com.evenly.group.application.dto;

import com.evenly.group.domain.Group;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "모임 정보")
public record GroupInfo(
        @Schema(description = "모임 ID", example = "550e8400-e29b-41d4-a716-446655440000") UUID id,
        @Schema(description = "모임 이름", example = "굿사이드 모임") String name,
        @Schema(description = "모임 소유자 ID") UUID ownerId,
        @Schema(description = "공유 토큰 (미발급 시 null)") String shareToken,
        @Schema(description = "생성 일시") OffsetDateTime createdAt) {

    public static GroupInfo from(Group group) {
        return new GroupInfo(
                group.getId(),
                group.getName().value(),
                group.getOwnerId(),
                group.getShareToken(),
                group.getCreatedAt());
    }
}
