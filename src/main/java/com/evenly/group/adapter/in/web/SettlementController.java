package com.evenly.group.adapter.in.web;

import com.evenly.group.application.dto.SettlementInfo;
import com.evenly.group.application.port.in.GetSettlementUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Settlement", description = "정산 API")
@RestController
@RequestMapping("/groups/{groupId}/settlement")
class SettlementController {

    private final GetSettlementUseCase getSettlementUseCase;

    SettlementController(GetSettlementUseCase getSettlementUseCase) {
        this.getSettlementUseCase = getSettlementUseCase;
    }

    @Operation(summary = "정산 결과 조회", description = "순잔액과 최소 송금안을 계산해 반환한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "모임을 찾을 수 없음")
    })
    @GetMapping
    public SettlementInfo getSettlement(@Parameter(description = "모임 ID") @PathVariable UUID groupId) {
        return getSettlementUseCase.getSettlement(groupId);
    }
}
