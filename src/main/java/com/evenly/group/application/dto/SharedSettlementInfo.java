package com.evenly.group.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "공유 링크용 읽기 전용 정산 결과")
public record SharedSettlementInfo(
        @Schema(description = "모임 ID") UUID groupId,
        @Schema(description = "모임 이름", example = "강릉 여행 모임") String groupName,
        @Schema(description = "정산 결과") SettlementInfo settlement) {}
