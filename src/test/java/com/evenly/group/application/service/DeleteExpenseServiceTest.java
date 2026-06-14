package com.evenly.group.application.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.evenly.common.domain.NotFoundException;
import com.evenly.group.application.port.out.LoadExpensePort;
import com.evenly.group.application.port.out.SaveExpensePort;
import com.evenly.group.domain.Expense;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteExpenseServiceTest {

    @Mock
    LoadExpensePort loadExpensePort;

    @Mock
    SaveExpensePort saveExpensePort;

    @InjectMocks
    DeleteExpenseService service;

    @Test
    void 지출을_삭제한다() {
        UUID groupId = UUID.randomUUID();
        UUID expenseId = UUID.randomUUID();
        UUID payer = UUID.randomUUID();
        when(loadExpensePort.findById(expenseId))
                .thenReturn(Optional.of(new Expense(expenseId, groupId, payer, "저녁", 100, List.of(payer))));

        service.deleteExpense(groupId, expenseId);

        verify(saveExpensePort).deleteById(expenseId);
    }

    @Test
    void 없는_지출이면_NotFound() {
        UUID expenseId = UUID.randomUUID();
        when(loadExpensePort.findById(expenseId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteExpense(UUID.randomUUID(), expenseId))
                .isInstanceOf(NotFoundException.class);
        verify(saveExpensePort, never()).deleteById(expenseId);
    }

    @Test
    void 다른_모임의_지출이면_NotFound() {
        UUID expenseId = UUID.randomUUID();
        UUID payer = UUID.randomUUID();
        when(loadExpensePort.findById(expenseId))
                .thenReturn(Optional.of(new Expense(expenseId, UUID.randomUUID(), payer, "저녁", 100, List.of(payer))));

        assertThatThrownBy(() -> service.deleteExpense(UUID.randomUUID(), expenseId))
                .isInstanceOf(NotFoundException.class);
        verify(saveExpensePort, never()).deleteById(expenseId);
    }
}
