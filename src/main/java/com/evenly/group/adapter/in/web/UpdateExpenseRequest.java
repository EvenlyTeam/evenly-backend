package com.evenly.group.adapter.in.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

@Schema(description = "지출 수정 요청 (전체 교체)")
public record UpdateExpenseRequest(
        @Schema(description = "결제자 참여자 ID") @NotNull(message = "결제자는 필수입니다") UUID payerId,
        @Schema(description = "내용(메모)", example = "저녁 고깃집")
                @NotBlank(message = "내용은 필수입니다") @Size(max = 100, message = "내용은 최대 100자입니다") String description,
        @Schema(description = "금액(원, 정수)", example = "120000") @Positive(message = "금액은 1원 이상이어야 합니다") long amount,
        @Schema(description = "분담 대상 참여자 ID 목록") @NotEmpty(message = "분담 대상은 최소 1명 이상이어야 합니다") List<@NotNull UUID> shareParticipantIds) {}
