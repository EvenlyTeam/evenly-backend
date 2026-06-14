package com.evenly.group.application.port.in;

import com.evenly.group.application.dto.CreateExpenseCommand;
import com.evenly.group.application.dto.ExpenseInfo;

public interface AddExpenseUseCase {

    ExpenseInfo addExpense(CreateExpenseCommand command);
}
