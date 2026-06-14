package com.evenly.group.adapter.in.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

@Schema(description = "모임 생성 요청")
public record CreateGroupRequest(
        @Schema(description = "모임 이름", example = "강릉 여행 모임")
                @NotBlank(message = "모임 이름은 필수입니다") @Size(max = 50, message = "모임 이름은 최대 50자입니다") String name,
        @Schema(description = "참여자 이름 목록 (선택)", example = "[\"준호\", \"민지\", \"서연\", \"태우\"]")
                @Size(max = 50, message = "참여자는 최대 50명입니다") List<@NotBlank(message = "참여자 이름은 비어 있을 수 없습니다") @Size(max = 50, message = "참여자 이름은 최대 50자입니다") String>
                        participants) {

    public CreateGroupRequest {
        participants = participants == null ? List.of() : participants;
    }
}
