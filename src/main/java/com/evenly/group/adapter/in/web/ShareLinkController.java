package com.evenly.group.adapter.in.web;

import com.evenly.group.application.GroupAccessGuard;
import com.evenly.group.application.dto.ShareLinkInfo;
import com.evenly.group.application.port.in.GenerateShareLinkUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Share", description = "공유 링크 API")
@RestController
@RequestMapping("/groups/{groupId}/share-link")
class ShareLinkController {

    private final GenerateShareLinkUseCase generateShareLinkUseCase;
    private final GroupAccessGuard groupAccessGuard;

    ShareLinkController(GenerateShareLinkUseCase generateShareLinkUseCase, GroupAccessGuard groupAccessGuard) {
        this.generateShareLinkUseCase = generateShareLinkUseCase;
        this.groupAccessGuard = groupAccessGuard;
    }

    @Operation(summary = "공유 링크 발급", description = "읽기 전용 공유 토큰을 발급한다(멱등 — 이미 있으면 기존 토큰 반환).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "발급 성공"),
        @ApiResponse(responseCode = "404", description = "모임을 찾을 수 없음")
    })
    @PostMapping
    public ShareLinkInfo generateShareLink(
            @Parameter(hidden = true) @AuthenticationPrincipal UUID requesterId,
            @Parameter(description = "모임 ID") @PathVariable UUID groupId) {
        groupAccessGuard.requireOwner(groupId, requesterId);
        return generateShareLinkUseCase.generateShareLink(groupId);
    }
}
