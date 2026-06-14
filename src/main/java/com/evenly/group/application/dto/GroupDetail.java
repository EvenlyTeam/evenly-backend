package com.evenly.group.application.dto;

import com.evenly.group.domain.Group;
import com.evenly.group.domain.Participant;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "모임 상세 (참여자 포함)")
public record GroupDetail(
        @Schema(description = "모임 ID", example = "550e8400-e29b-41d4-a716-446655440000") UUID id,
        @Schema(description = "모임 이름", example = "강릉 여행 모임") String name,
        @Schema(description = "모임 소유자 ID") UUID ownerId,
        @Schema(description = "공유 토큰 (미발급 시 null)") String shareToken,
        @Schema(description = "생성 일시") OffsetDateTime createdAt,
        @Schema(description = "참여자 목록") List<ParticipantInfo> participants) {

    public static GroupDetail from(Group group, List<Participant> participants) {
        return new GroupDetail(
                group.getId(),
                group.getName().value(),
                group.getOwnerId(),
                group.getShareToken(),
                group.getCreatedAt(),
                participants.stream().map(ParticipantInfo::from).toList());
    }
}
