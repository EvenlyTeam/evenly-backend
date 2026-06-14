package com.evenly.group.application.port.in;

import java.util.UUID;

public interface DeleteExpenseUseCase {

    void deleteExpense(UUID groupId, UUID expenseId);
}
