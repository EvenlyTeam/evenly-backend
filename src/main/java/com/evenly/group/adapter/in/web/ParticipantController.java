package com.evenly.group.adapter.in.web;

import com.evenly.group.application.GroupAccessGuard;
import com.evenly.group.application.dto.AddParticipantCommand;
import com.evenly.group.application.dto.ParticipantInfo;
import com.evenly.group.application.port.in.AddParticipantUseCase;
import com.evenly.group.application.port.in.RemoveParticipantUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Participants", description = "참여자 API")
@RestController
@RequestMapping("/groups/{groupId}/participants")
class ParticipantController {

    private final AddParticipantUseCase addParticipantUseCase;
    private final RemoveParticipantUseCase removeParticipantUseCase;
    private final GroupAccessGuard groupAccessGuard;

    ParticipantController(
            AddParticipantUseCase addParticipantUseCase,
            RemoveParticipantUseCase removeParticipantUseCase,
            GroupAccessGuard groupAccessGuard) {
        this.addParticipantUseCase = addParticipantUseCase;
        this.removeParticipantUseCase = removeParticipantUseCase;
        this.groupAccessGuard = groupAccessGuard;
    }

    @Operation(summary = "참여자 추가")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "추가 성공"),
        @ApiResponse(responseCode = "404", description = "모임을 찾을 수 없음"),
        @ApiResponse(responseCode = "409", description = "이미 존재하는 참여자 이름")
    })
    @PostMapping
    public ResponseEntity<ParticipantInfo> addParticipant(
            @Parameter(hidden = true) @AuthenticationPrincipal UUID requesterId,
            @Parameter(description = "모임 ID") @PathVariable UUID groupId,
            @Valid @RequestBody AddParticipantRequest request) {
        groupAccessGuard.requireOwner(groupId, requesterId);
        ParticipantInfo created =
                addParticipantUseCase.addParticipant(new AddParticipantCommand(groupId, request.name()));
        return ResponseEntity.created(URI.create("/groups/" + groupId + "/participants/" + created.id()))
                .body(created);
    }

    @Operation(summary = "참여자 삭제")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "참여자를 찾을 수 없음"),
        @ApiResponse(responseCode = "409", description = "지출에 연결된 참여자는 삭제할 수 없음")
    })
    @DeleteMapping("/{participantId}")
    public ResponseEntity<Void> removeParticipant(
            @Parameter(hidden = true) @AuthenticationPrincipal UUID requesterId,
            @Parameter(description = "모임 ID") @PathVariable UUID groupId,
            @Parameter(description = "참여자 ID") @PathVariable UUID participantId) {
        groupAccessGuard.requireOwner(groupId, requesterId);
        removeParticipantUseCase.removeParticipant(groupId, participantId);
        return ResponseEntity.noContent().build();
    }
}
