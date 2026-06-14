package com.evenly.group.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "모임 요약 (목록용)")
public record GroupSummary(
        @Schema(description = "모임 ID") UUID id,
        @Schema(description = "모임 이름", example = "강릉 여행 모임") String name,
        @Schema(description = "참여자 수", example = "4") int participantCount,
        @Schema(description = "생성 일시") OffsetDateTime createdAt) {}
