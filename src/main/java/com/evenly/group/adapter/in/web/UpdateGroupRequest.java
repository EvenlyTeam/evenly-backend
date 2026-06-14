package com.evenly.group.adapter.in.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "모임 수정 요청")
public record UpdateGroupRequest(
        @Schema(description = "모임 이름", example = "강릉 여행 모임")
                @NotBlank(message = "모임 이름은 필수입니다") @Size(max = 50, message = "모임 이름은 최대 50자입니다") String name) {}
