package com.evenly.group.application.port.out;

import com.evenly.group.domain.Expense;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoadExpensePort {

    List<Expense> findByGroupId(UUID groupId);

    Optional<Expense> findById(UUID id);
}
