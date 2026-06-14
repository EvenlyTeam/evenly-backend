package com.evenly.group.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.evenly.common.domain.NotFoundException;
import com.evenly.group.application.dto.CreateExpenseCommand;
import com.evenly.group.application.dto.ExpenseInfo;
import com.evenly.group.application.port.out.LoadGroupPort;
import com.evenly.group.application.port.out.LoadParticipantPort;
import com.evenly.group.application.port.out.SaveExpensePort;
import com.evenly.group.domain.Group;
import com.evenly.group.domain.Participant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AddExpenseServiceTest {

    @Mock
    LoadGroupPort loadGroupPort;

    @Mock
    LoadParticipantPort loadParticipantPort;

    @Mock
    SaveExpensePort saveExpensePort;

    @InjectMocks
    AddExpenseService service;

    final UUID groupId = UUID.randomUUID();
    final UUID payer = UUID.randomUUID();
    final UUID member2 = UUID.randomUUID();
    final UUID outsider = UUID.randomUUID();

    @BeforeEach
    void stubGroupAndMembers() {
        // strict stubs: 개별 테스트에서 필요한 것만 — lenient 회피 위해 여기선 stub 안 함
    }

    private void groupExistsWithMembers() {
        when(loadGroupPort.findById(groupId))
                .thenReturn(Optional.of(new Group(groupId, "g", UUID.randomUUID(), null, null)));
        when(loadParticipantPort.findByGroupId(groupId))
                .thenReturn(List.of(
                        new Participant(payer, groupId, null, "준호"), new Participant(member2, groupId, null, "민지")));
    }

    @Test
    void 지출을_추가한다() {
        groupExistsWithMembers();
        when(saveExpensePort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ExpenseInfo result =
                service.addExpense(new CreateExpenseCommand(groupId, payer, "저녁", 120000, List.of(payer, member2)));

        assertThat(result.amount()).isEqualTo(120000);
        assertThat(result.shareParticipantIds()).containsExactly(payer, member2);
    }

    @Test
    void 없는_모임이면_NotFound() {
        when(loadGroupPort.findById(groupId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.addExpense(new CreateExpenseCommand(groupId, payer, "x", 100, List.of(payer))))
                .isInstanceOf(NotFoundException.class);
        verify(saveExpensePort, never()).save(any());
    }

    @Test
    void 결제자가_참여자가_아니면_400() {
        groupExistsWithMembers();

        assertThatThrownBy(
                        () -> service.addExpense(new CreateExpenseCommand(groupId, outsider, "x", 100, List.of(payer))))
                .isInstanceOf(IllegalArgumentException.class);
        verify(saveExpensePort, never()).save(any());
    }

    @Test
    void 분담대상이_참여자가_아니면_400() {
        groupExistsWithMembers();

        assertThatThrownBy(() -> service.addExpense(
                        new CreateExpenseCommand(groupId, payer, "x", 100, List.of(payer, outsider))))
                .isInstanceOf(IllegalArgumentException.class);
        verify(saveExpensePort, never()).save(any());
    }

    @Test
    void 분담대상이_중복이면_400() {
        groupExistsWithMembers();

        assertThatThrownBy(() ->
                        service.addExpense(new CreateExpenseCommand(groupId, payer, "x", 100, List.of(payer, payer))))
                .isInstanceOf(IllegalArgumentException.class);
        verify(saveExpensePort, never()).save(any());
    }

    @Test
    void 분담대상이_비면_400() {
        groupExistsWithMembers();

        assertThatThrownBy(() -> service.addExpense(new CreateExpenseCommand(groupId, payer, "x", 100, List.of())))
                .isInstanceOf(IllegalArgumentException.class);
        verify(saveExpensePort, never()).save(any());
    }
}
