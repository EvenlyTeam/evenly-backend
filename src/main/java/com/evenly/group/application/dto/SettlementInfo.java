package com.evenly.group.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(description = "정산 결과")
public record SettlementInfo(
        @Schema(description = "총 지출액(원)", example = "392000") long total,
        @Schema(description = "참여자별 순잔액") List<BalanceInfo> balances,
        @Schema(description = "최소 송금 리스트") List<TransferInfo> transfers) {

    @Schema(description = "참여자 순잔액 (양수=받을 돈, 음수=줄 돈)")
    public record BalanceInfo(
            @Schema(description = "참여자 ID") UUID participantId,
            @Schema(description = "참여자 이름") String name,
            @Schema(description = "순잔액(원)") long net) {}

    @Schema(description = "송금 1건")
    public record TransferInfo(
            @Schema(description = "보내는 참여자 ID") UUID fromParticipantId,
            @Schema(description = "보내는 참여자 이름") String fromName,
            @Schema(description = "받는 참여자 ID") UUID toParticipantId,
            @Schema(description = "받는 참여자 이름") String toName,
            @Schema(description = "송금액(원)") long amount) {}
}
