package com.evenly.group.adapter.in.web;

import com.evenly.group.application.dto.CreateGroupCommand;
import com.evenly.group.application.dto.GroupInfo;
import com.evenly.group.application.port.in.CreateGroupUseCase;
import com.evenly.group.application.port.in.GetGroupUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Groups", description = "모임 API")
@RestController
@RequestMapping("/groups")
class GroupController {

    private final CreateGroupUseCase createGroupUseCase;
    private final GetGroupUseCase getGroupUseCase;

    GroupController(CreateGroupUseCase createGroupUseCase, GetGroupUseCase getGroupUseCase) {
        this.createGroupUseCase = createGroupUseCase;
        this.getGroupUseCase = getGroupUseCase;
    }

    @Operation(summary = "모임 생성")
    @ApiResponse(responseCode = "201", description = "생성 성공")
    @PostMapping
    public ResponseEntity<GroupInfo> createGroup(
            // TODO(auth): 인증 어댑터 구현 후 인증 principal 에서 ownerId 주입으로 교체
            @Parameter(description = "소유자 사용자 ID (임시: 인증 전까지 헤더로 전달)") @RequestHeader("X-User-Id") UUID ownerId,
            @Valid @RequestBody CreateGroupRequest request) {
        GroupInfo created = createGroupUseCase.createGroup(new CreateGroupCommand(request.name(), ownerId));
        return ResponseEntity.created(URI.create("/groups/" + created.id())).body(created);
    }

    @Operation(summary = "모임 단건 조회")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "모임을 찾을 수 없음", content = @Content)
    })
    @GetMapping("/{id}")
    public GroupInfo getGroup(@Parameter(description = "모임 ID") @PathVariable UUID id) {
        return getGroupUseCase.getGroup(id);
    }
}
