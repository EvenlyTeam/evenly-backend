package com.evenly.group.application.port.in;

import com.evenly.group.application.dto.ExpenseInfo;
import java.util.List;
import java.util.UUID;

public interface ListExpensesUseCase {

    List<ExpenseInfo> listExpenses(UUID groupId);
}
