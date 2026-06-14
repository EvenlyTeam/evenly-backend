package com.evenly.user.adapter.in.web;

import com.evenly.user.application.dto.UserInfo;
import com.evenly.user.application.port.in.DeleteUserUseCase;
import com.evenly.user.application.port.in.GetUserUseCase;
import com.evenly.user.application.port.in.UpdateUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Users", description = "사용자 API")
@RestController
@RequestMapping("/users")
class UserController {

    private final GetUserUseCase getUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;

    UserController(
            GetUserUseCase getUserUseCase, UpdateUserUseCase updateUserUseCase, DeleteUserUseCase deleteUserUseCase) {
        this.getUserUseCase = getUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
    }

    @Operation(summary = "내 정보 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/me")
    public UserInfo getMe(@Parameter(hidden = true) @AuthenticationPrincipal UUID userId) {
        return getUserUseCase.getUser(userId);
    }

    @Operation(summary = "내 정보 수정", description = "표시 이름(닉네임)을 변경한다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @PatchMapping("/me")
    public UserInfo updateMe(
            @Parameter(hidden = true) @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody UpdateProfileRequest request) {
        return updateUserUseCase.updateDisplayName(userId, request.displayName());
    }

    @Operation(summary = "회원 탈퇴", description = "계정과 본인이 소유한 모임을 삭제한다.")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "탈퇴 성공")})
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMe(@Parameter(hidden = true) @AuthenticationPrincipal UUID userId) {
        deleteUserUseCase.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
