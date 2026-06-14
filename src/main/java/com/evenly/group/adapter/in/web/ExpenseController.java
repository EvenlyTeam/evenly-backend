package com.evenly.group.adapter.in.web;

import com.evenly.group.application.GroupAccessGuard;
import com.evenly.group.application.dto.CreateExpenseCommand;
import com.evenly.group.application.dto.ExpenseInfo;
import com.evenly.group.application.dto.UpdateExpenseCommand;
import com.evenly.group.application.port.in.AddExpenseUseCase;
import com.evenly.group.application.port.in.DeleteExpenseUseCase;
import com.evenly.group.application.port.in.ListExpensesUseCase;
import com.evenly.group.application.port.in.UpdateExpenseUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

@Tag(name = "Expenses", description = "지출 항목 API")
@RestController
@RequestMapping("/groups/{groupId}/expenses")
class ExpenseController {

    private final AddExpenseUseCase addExpenseUseCase;
    private final ListExpensesUseCase listExpensesUseCase;
    private final UpdateExpenseUseCase updateExpenseUseCase;
    private final DeleteExpenseUseCase deleteExpenseUseCase;
    private final GroupAccessGuard groupAccessGuard;

    ExpenseController(
            AddExpenseUseCase addExpenseUseCase,
            ListExpensesUseCase listExpensesUseCase,
            UpdateExpenseUseCase updateExpenseUseCase,
            DeleteExpenseUseCase deleteExpenseUseCase,
            GroupAccessGuard groupAccessGuard) {
        this.addExpenseUseCase = addExpenseUseCase;
        this.listExpensesUseCase = listExpensesUseCase;
        this.updateExpenseUseCase = updateExpenseUseCase;
        this.deleteExpenseUseCase = deleteExpenseUseCase;
        this.groupAccessGuard = groupAccessGuard;
    }

    @Operation(summary = "지출 추가", description = "결제자/금액/내용/분담대상으로 지출 항목을 추가한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "추가 성공"),
        @ApiResponse(responseCode = "400", description = "결제자/분담대상이 모임 참여자가 아님"),
        @ApiResponse(responseCode = "404", description = "모임을 찾을 수 없음")
    })
    @PostMapping
    public ResponseEntity<ExpenseInfo> addExpense(
            @Parameter(hidden = true) @AuthenticationPrincipal UUID requesterId,
            @Parameter(description = "모임 ID") @PathVariable UUID groupId,
            @Valid @RequestBody CreateExpenseRequest request) {
        groupAccessGuard.requireOwner(groupId, requesterId);
        ExpenseInfo created = addExpenseUseCase.addExpense(new CreateExpenseCommand(
                groupId, request.payerId(), request.description(), request.amount(), request.shareParticipantIds()));
        return ResponseEntity.created(URI.create("/groups/" + groupId + "/expenses/" + created.id()))
                .body(created);
    }

    @Operation(summary = "지출 목록 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public List<ExpenseInfo> listExpenses(
            @Parameter(hidden = true) @AuthenticationPrincipal UUID requesterId,
            @Parameter(description = "모임 ID") @PathVariable UUID groupId) {
        groupAccessGuard.requireOwner(groupId, requesterId);
        return listExpensesUseCase.listExpenses(groupId);
    }

    @Operation(summary = "지출 수정", description = "지출 항목을 전체 교체한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "400", description = "결제자/분담대상이 모임 참여자가 아님"),
        @ApiResponse(responseCode = "404", description = "지출을 찾을 수 없음")
    })
    @PatchMapping("/{expenseId}")
    public ExpenseInfo updateExpense(
            @Parameter(hidden = true) @AuthenticationPrincipal UUID requesterId,
            @Parameter(description = "모임 ID") @PathVariable UUID groupId,
            @Parameter(description = "지출 ID") @PathVariable UUID expenseId,
            @Valid @RequestBody UpdateExpenseRequest request) {
        groupAccessGuard.requireOwner(groupId, requesterId);
        return updateExpenseUseCase.updateExpense(new UpdateExpenseCommand(
                groupId,
                expenseId,
                request.payerId(),
                request.description(),
                request.amount(),
                request.shareParticipantIds()));
    }

    @Operation(summary = "지출 삭제")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "지출을 찾을 수 없음")
    })
    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Void> deleteExpense(
            @Parameter(hidden = true) @AuthenticationPrincipal UUID requesterId,
            @Parameter(description = "모임 ID") @PathVariable UUID groupId,
            @Parameter(description = "지출 ID") @PathVariable UUID expenseId) {
        groupAccessGuard.requireOwner(groupId, requesterId);
        deleteExpenseUseCase.deleteExpense(groupId, expenseId);
        return ResponseEntity.noContent().build();
    }
}
