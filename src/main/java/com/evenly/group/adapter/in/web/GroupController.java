package com.evenly.group.adapter.in.web;

import com.evenly.group.application.GroupAccessGuard;
import com.evenly.group.application.dto.CreateGroupCommand;
import com.evenly.group.application.dto.GroupDetail;
import com.evenly.group.application.dto.GroupSummary;
import com.evenly.group.application.port.in.CreateGroupUseCase;
import com.evenly.group.application.port.in.DeleteGroupUseCase;
import com.evenly.group.application.port.in.GetGroupUseCase;
import com.evenly.group.application.port.in.ListGroupsUseCase;
import com.evenly.group.application.port.in.SettleGroupUseCase;
import com.evenly.group.application.port.in.UpdateGroupUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Groups", description = "모임 API")
@RestController
@RequestMapping("/groups")
class GroupController {

    private final CreateGroupUseCase createGroupUseCase;
    private final GetGroupUseCase getGroupUseCase;
    private final ListGroupsUseCase listGroupsUseCase;
    private final SettleGroupUseCase settleGroupUseCase;
    private final UpdateGroupUseCase updateGroupUseCase;
    private final DeleteGroupUseCase deleteGroupUseCase;
    private final GroupAccessGuard groupAccessGuard;

    GroupController(
            CreateGroupUseCase createGroupUseCase,
            GetGroupUseCase getGroupUseCase,
            ListGroupsUseCase listGroupsUseCase,
            SettleGroupUseCase settleGroupUseCase,
            UpdateGroupUseCase updateGroupUseCase,
            DeleteGroupUseCase deleteGroupUseCase,
            GroupAccessGuard groupAccessGuard) {
        this.createGroupUseCase = createGroupUseCase;
        this.getGroupUseCase = getGroupUseCase;
        this.listGroupsUseCase = listGroupsUseCase;
        this.settleGroupUseCase = settleGroupUseCase;
        this.updateGroupUseCase = updateGroupUseCase;
        this.deleteGroupUseCase = deleteGroupUseCase;
        this.groupAccessGuard = groupAccessGuard;
    }

    @Operation(summary = "모임 생성", description = "이름과 참여자(선택)로 모임을 생성한다.")
    @ApiResponse(responseCode = "201", description = "생성 성공")
    @PostMapping
    public ResponseEntity<GroupDetail> createGroup(
            @Parameter(hidden = true) @AuthenticationPrincipal UUID ownerId,
            @Valid @RequestBody CreateGroupRequest request) {
        GroupDetail created =
                createGroupUseCase.createGroup(new CreateGroupCommand(request.name(), ownerId, request.participants()));
        return ResponseEntity.created(URI.create("/groups/" + created.id())).body(created);
    }

    @Operation(summary = "내 모임 목록 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public List<GroupSummary> listGroups(@Parameter(hidden = true) @AuthenticationPrincipal UUID ownerId) {
        return listGroupsUseCase.listGroups(ownerId);
    }

    @Operation(summary = "모임 단건 조회", description = "참여자 목록을 포함한 모임 상세를 반환한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "모임을 찾을 수 없음", content = @Content)
    })
    @GetMapping("/{id}")
    public GroupDetail getGroup(
            @Parameter(hidden = true) @AuthenticationPrincipal UUID requesterId,
            @Parameter(description = "모임 ID") @PathVariable UUID id) {
        groupAccessGuard.requireOwner(id, requesterId);
        return getGroupUseCase.getGroup(id);
    }

    @Operation(summary = "모임 수정", description = "모임 이름을 변경한다(소유자).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "403", description = "소유자가 아님"),
        @ApiResponse(responseCode = "404", description = "모임을 찾을 수 없음")
    })
    @PatchMapping("/{id}")
    public GroupDetail updateGroup(
            @Parameter(hidden = true) @AuthenticationPrincipal UUID requesterId,
            @Parameter(description = "모임 ID") @PathVariable UUID id,
            @Valid @RequestBody UpdateGroupRequest request) {
        groupAccessGuard.requireOwner(id, requesterId);
        return updateGroupUseCase.rename(id, request.name());
    }

    @Operation(summary = "모임 삭제", description = "모임과 참여자/지출을 삭제한다(소유자).")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "403", description = "소유자가 아님"),
        @ApiResponse(responseCode = "404", description = "모임을 찾을 수 없음")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(
            @Parameter(hidden = true) @AuthenticationPrincipal UUID requesterId,
            @Parameter(description = "모임 ID") @PathVariable UUID id) {
        groupAccessGuard.requireOwner(id, requesterId);
        deleteGroupUseCase.deleteGroup(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "정산 완료 표시", description = "실제 송금을 마쳤음을 표시한다(소유자).")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "처리 성공"),
        @ApiResponse(responseCode = "403", description = "소유자가 아님"),
        @ApiResponse(responseCode = "404", description = "모임을 찾을 수 없음")
    })
    @PostMapping("/{id}/settle")
    public ResponseEntity<Void> settle(
            @Parameter(hidden = true) @AuthenticationPrincipal UUID requesterId,
            @Parameter(description = "모임 ID") @PathVariable UUID id) {
        groupAccessGuard.requireOwner(id, requesterId);
        settleGroupUseCase.settle(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "정산 완료 해제", description = "정산 완료 상태를 되돌린다(소유자).")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "처리 성공"),
        @ApiResponse(responseCode = "403", description = "소유자가 아님"),
        @ApiResponse(responseCode = "404", description = "모임을 찾을 수 없음")
    })
    @PostMapping("/{id}/reopen")
    public ResponseEntity<Void> reopen(
            @Parameter(hidden = true) @AuthenticationPrincipal UUID requesterId,
            @Parameter(description = "모임 ID") @PathVariable UUID id) {
        groupAccessGuard.requireOwner(id, requesterId);
        settleGroupUseCase.reopen(id);
        return ResponseEntity.noContent().build();
    }
}
