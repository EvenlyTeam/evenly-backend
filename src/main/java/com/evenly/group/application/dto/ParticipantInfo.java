package com.evenly.group.application.dto;

import com.evenly.group.domain.Participant;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "참여자 정보")
public record ParticipantInfo(
        @Schema(description = "참여자 ID") UUID id,
        @Schema(description = "참여자 이름", example = "준호") String name,
        @Schema(description = "연결된 사용자 ID (이름만 참여자는 null)") UUID userId) {

    public static ParticipantInfo from(Participant participant) {
        return new ParticipantInfo(participant.getId(), participant.getName().value(), participant.getUserId());
    }
}
