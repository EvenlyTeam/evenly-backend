package com.evenly.group.application.service;

import com.evenly.common.domain.NotFoundException;
import com.evenly.group.application.port.in.DeleteExpenseUseCase;
import com.evenly.group.application.port.out.LoadExpensePort;
import com.evenly.group.application.port.out.SaveExpensePort;
import com.evenly.group.domain.Expense;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class DeleteExpenseService implements DeleteExpenseUseCase {

    private final LoadExpensePort loadExpensePort;
    private final SaveExpensePort saveExpensePort;

    DeleteExpenseService(LoadExpensePort loadExpensePort, SaveExpensePort saveExpensePort) {
        this.loadExpensePort = loadExpensePort;
        this.saveExpensePort = saveExpensePort;
    }

    @Override
    public void deleteExpense(UUID groupId, UUID expenseId) {
        Expense expense =
                loadExpensePort.findById(expenseId).orElseThrow(() -> new NotFoundException("Expense", expenseId));
        if (!expense.belongsTo(groupId)) {
            throw new NotFoundException("Expense", expenseId);
        }
        saveExpensePort.deleteById(expenseId);
    }
}
