package com.evenly.group.adapter.in.web;

import com.evenly.group.application.dto.SharedSettlementInfo;
import com.evenly.group.application.port.in.GetSharedSettlementUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 비로그인 사용자가 공유 토큰으로 정산 결과만 읽는 공개 엔드포인트. */
@Tag(name = "Shared", description = "공유 링크 열람 API (비로그인)")
@RestController
@RequestMapping("/shared")
class SharedController {

    private final GetSharedSettlementUseCase getSharedSettlementUseCase;

    SharedController(GetSharedSettlementUseCase getSharedSettlementUseCase) {
        this.getSharedSettlementUseCase = getSharedSettlementUseCase;
    }

    @Operation(summary = "공유 링크로 정산 결과 열람", description = "공유 토큰으로 읽기 전용 정산 결과를 반환한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "유효하지 않은 공유 토큰")
    })
    @GetMapping("/{shareToken}")
    public SharedSettlementInfo getSharedSettlement(@Parameter(description = "공유 토큰") @PathVariable String shareToken) {
        return getSharedSettlementUseCase.getSharedSettlement(shareToken);
    }
}
