package com.evenly.group.application.dto;

import com.evenly.group.domain.Expense;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(description = "지출 항목 정보")
public record ExpenseInfo(
        @Schema(description = "지출 ID") UUID id,
        @Schema(description = "모임 ID") UUID groupId,
        @Schema(description = "결제자 참여자 ID") UUID payerId,
        @Schema(description = "내용(메모)", example = "저녁 고깃집") String description,
        @Schema(description = "금액(원, 정수)", example = "120000") long amount,
        @Schema(description = "분담 대상 참여자 ID 목록") List<UUID> shareParticipantIds) {

    public static ExpenseInfo from(Expense expense) {
        return new ExpenseInfo(
                expense.getId(),
                expense.getGroupId(),
                expense.getPayerId(),
                expense.getDescription(),
                expense.getAmount(),
                expense.getShareParticipantIds());
    }
}
