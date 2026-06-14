package com.evenly.group.application.port.in;

import com.evenly.group.application.dto.ExpenseInfo;
import com.evenly.group.application.dto.UpdateExpenseCommand;

public interface UpdateExpenseUseCase {

    ExpenseInfo updateExpense(UpdateExpenseCommand command);
}
