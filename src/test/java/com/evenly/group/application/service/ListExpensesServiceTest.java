package com.evenly.group.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.evenly.group.application.dto.ExpenseInfo;
import com.evenly.group.application.port.out.LoadExpensePort;
import com.evenly.group.domain.Expense;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListExpensesServiceTest {

    @Mock
    LoadExpensePort loadExpensePort;

    @InjectMocks
    ListExpensesService service;

    @Test
    void 모임의_지출을_반환한다() {
        UUID groupId = UUID.randomUUID();
        UUID payer = UUID.randomUUID();
        when(loadExpensePort.findByGroupId(groupId))
                .thenReturn(List.of(new Expense(UUID.randomUUID(), groupId, payer, "저녁", 120000, List.of(payer))));

        List<ExpenseInfo> result = service.listExpenses(groupId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).description()).isEqualTo("저녁");
    }
}
