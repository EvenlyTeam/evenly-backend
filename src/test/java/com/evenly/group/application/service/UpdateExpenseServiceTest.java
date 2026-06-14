package com.evenly.group.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.evenly.common.domain.NotFoundException;
import com.evenly.group.application.dto.ExpenseInfo;
import com.evenly.group.application.dto.UpdateExpenseCommand;
import com.evenly.group.application.port.out.LoadExpensePort;
import com.evenly.group.application.port.out.LoadParticipantPort;
import com.evenly.group.application.port.out.SaveExpensePort;
import com.evenly.group.domain.Expense;
import com.evenly.group.domain.Participant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateExpenseServiceTest {

    @Mock
    LoadExpensePort loadExpensePort;

    @Mock
    LoadParticipantPort loadParticipantPort;

    @Mock
    SaveExpensePort saveExpensePort;

    @InjectMocks
    UpdateExpenseService service;

    final UUID groupId = UUID.randomUUID();
    final UUID expenseId = UUID.randomUUID();
    final UUID payer = UUID.randomUUID();
    final UUID member2 = UUID.randomUUID();

    private Expense existing() {
        return new Expense(expenseId, groupId, payer, "저녁", 100, List.of(payer));
    }

    private void membersExist() {
        when(loadParticipantPort.findByGroupId(groupId))
                .thenReturn(List.of(
                        new Participant(payer, groupId, null, "준호"), new Participant(member2, groupId, null, "민지")));
    }

    @Test
    void 지출을_수정한다() {
        when(loadExpensePort.findById(expenseId)).thenReturn(Optional.of(existing()));
        membersExist();
        when(saveExpensePort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ExpenseInfo result = service.updateExpense(
                new UpdateExpenseCommand(groupId, expenseId, member2, "택시", 30000, List.of(payer, member2)));

        assertThat(result.amount()).isEqualTo(30000);
        assertThat(result.payerId()).isEqualTo(member2);
        assertThat(result.id()).isEqualTo(expenseId);
    }

    @Test
    void 없는_지출이면_NotFound() {
        when(loadExpensePort.findById(expenseId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateExpense(
                        new UpdateExpenseCommand(groupId, expenseId, payer, "x", 100, List.of(payer))))
                .isInstanceOf(NotFoundException.class);
        verify(saveExpensePort, never()).save(any());
    }

    @Test
    void 다른_모임의_지출이면_NotFound() {
        when(loadExpensePort.findById(expenseId))
                .thenReturn(Optional.of(new Expense(expenseId, UUID.randomUUID(), payer, "저녁", 100, List.of(payer))));

        assertThatThrownBy(() -> service.updateExpense(
                        new UpdateExpenseCommand(groupId, expenseId, payer, "x", 100, List.of(payer))))
                .isInstanceOf(NotFoundException.class);
        verify(saveExpensePort, never()).save(any());
    }

    @Test
    void 결제자가_참여자가_아니면_400() {
        when(loadExpensePort.findById(expenseId)).thenReturn(Optional.of(existing()));
        membersExist();

        assertThatThrownBy(() -> service.updateExpense(
                        new UpdateExpenseCommand(groupId, expenseId, UUID.randomUUID(), "x", 100, List.of(payer))))
                .isInstanceOf(IllegalArgumentException.class);
        verify(saveExpensePort, never()).save(any());
    }
}
