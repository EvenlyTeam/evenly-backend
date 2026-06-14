package com.evenly.group.application.service;

import com.evenly.group.application.dto.ExpenseInfo;
import com.evenly.group.application.port.in.ListExpensesUseCase;
import com.evenly.group.application.port.out.LoadExpensePort;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
class ListExpensesService implements ListExpensesUseCase {

    private final LoadExpensePort loadExpensePort;

    ListExpensesService(LoadExpensePort loadExpensePort) {
        this.loadExpensePort = loadExpensePort;
    }

    @Override
    public List<ExpenseInfo> listExpenses(UUID groupId) {
        return loadExpensePort.findByGroupId(groupId).stream()
                .map(ExpenseInfo::from)
                .toList();
    }
}
