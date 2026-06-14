package com.evenly.group.application.service;

import com.evenly.common.domain.NotFoundException;
import com.evenly.group.application.dto.ExpenseInfo;
import com.evenly.group.application.dto.UpdateExpenseCommand;
import com.evenly.group.application.port.in.UpdateExpenseUseCase;
import com.evenly.group.application.port.out.LoadExpensePort;
import com.evenly.group.application.port.out.LoadParticipantPort;
import com.evenly.group.application.port.out.SaveExpensePort;
import com.evenly.group.domain.Expense;
import com.evenly.group.domain.Participant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class UpdateExpenseService implements UpdateExpenseUseCase {

    private final LoadExpensePort loadExpensePort;
    private final LoadParticipantPort loadParticipantPort;
    private final SaveExpensePort saveExpensePort;

    UpdateExpenseService(
            LoadExpensePort loadExpensePort, LoadParticipantPort loadParticipantPort, SaveExpensePort saveExpensePort) {
        this.loadExpensePort = loadExpensePort;
        this.loadParticipantPort = loadParticipantPort;
        this.saveExpensePort = saveExpensePort;
    }

    @Override
    public ExpenseInfo updateExpense(UpdateExpenseCommand command) {
        Expense expense = loadExpensePort
                .findById(command.expenseId())
                .orElseThrow(() -> new NotFoundException("Expense", command.expenseId()));
        if (!expense.belongsTo(command.groupId())) {
            throw new NotFoundException("Expense", command.expenseId());
        }

        Set<UUID> memberIds = loadParticipantPort.findByGroupId(command.groupId()).stream()
                .map(Participant::getId)
                .collect(Collectors.toSet());
        List<UUID> shares = command.shareParticipantIds();
        if (!memberIds.contains(command.payerId())) {
            throw new IllegalArgumentException("결제자가 모임 참여자가 아닙니다: " + command.payerId());
        }
        if (shares.isEmpty()) {
            throw new IllegalArgumentException("분담 대상이 비어 있습니다");
        }
        if (shares.stream().distinct().count() != shares.size()) {
            throw new IllegalArgumentException("분담 대상이 중복되었습니다");
        }
        for (UUID shareId : shares) {
            if (!memberIds.contains(shareId)) {
                throw new IllegalArgumentException("분담 대상이 모임 참여자가 아닙니다: " + shareId);
            }
        }

        Expense updated = expense.update(command.payerId(), command.description(), command.amount(), shares);
        return ExpenseInfo.from(saveExpensePort.save(updated));
    }
}
