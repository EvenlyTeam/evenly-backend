package com.evenly.group.application.port.out;

import com.evenly.group.domain.Expense;
import java.util.UUID;

public interface SaveExpensePort {

    Expense save(Expense expense);

    void deleteById(UUID id);
}
