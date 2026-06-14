package com.evenly.common.adapter.in.web;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

@Schema(description = "헬스 체크 응답")
public record HealthResponse(
        @Schema(description = "상태", example = "UP") String status,
        @Schema(description = "서비스 이름", example = "evenly-backend") String service,
        @Schema(description = "응답 시각") OffsetDateTime time) {}
