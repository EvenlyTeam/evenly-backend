package com.evenly.group.adapter.in.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "참여자 추가 요청")
public record AddParticipantRequest(
        @Schema(description = "참여자 이름", example = "지호")
                @NotBlank(message = "참여자 이름은 필수입니다") @Size(max = 50, message = "참여자 이름은 최대 50자입니다") String name) {}
