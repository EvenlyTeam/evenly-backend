package com.evenly.group.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.evenly.common.domain.NotFoundException;
import com.evenly.group.application.dto.SettlementInfo;
import com.evenly.group.application.port.out.LoadExpensePort;
import com.evenly.group.application.port.out.LoadGroupPort;
import com.evenly.group.application.port.out.LoadParticipantPort;
import com.evenly.group.domain.Expense;
import com.evenly.group.domain.Group;
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
class GetSettlementServiceTest {

    @Mock
    LoadGroupPort loadGroupPort;

    @Mock
    LoadParticipantPort loadParticipantPort;

    @Mock
    LoadExpensePort loadExpensePort;

    @InjectMocks
    GetSettlementService service;

    @Test
    void 순잔액과_최소송금을_이름과_함께_계산한다() {
        UUID groupId = UUID.randomUUID();
        UUID junho = UUID.randomUUID();
        UUID minji = UUID.randomUUID();
        UUID taewoo = UUID.randomUUID();

        when(loadGroupPort.findById(groupId))
                .thenReturn(Optional.of(new Group(groupId, "g", UUID.randomUUID(), null, null)));
        when(loadParticipantPort.findByGroupId(groupId))
                .thenReturn(List.of(
                        new Participant(junho, groupId, null, "준호"),
                        new Participant(minji, groupId, null, "민지"),
                        new Participant(taewoo, groupId, null, "태우")));
        // 민지가 90,000 결제, 3명 분담(각 30,000) → 민지 +60,000 / 준호 -30,000 / 태우 -30,000
        when(loadExpensePort.findByGroupId(groupId))
                .thenReturn(List.of(
                        new Expense(UUID.randomUUID(), groupId, minji, "저녁", 90000, List.of(junho, minji, taewoo))));

        SettlementInfo result = service.getSettlement(groupId);

        assertThat(result.total()).isEqualTo(90000);
        assertThat(result.balances()).extracting("net").containsExactlyInAnyOrder(-30000L, 60000L, -30000L);
        // 송금 2건, 모두 민지에게
        assertThat(result.transfers()).hasSize(2);
        assertThat(result.transfers()).allSatisfy(t -> assertThat(t.toName()).isEqualTo("민지"));
        long sumToMinji = result.transfers().stream()
                .mapToLong(SettlementInfo.TransferInfo::amount)
                .sum();
        assertThat(sumToMinji).isEqualTo(60000);
    }

    @Test
    void 없는_모임이면_NotFound() {
        UUID groupId = UUID.randomUUID();
        when(loadGroupPort.findById(groupId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getSettlement(groupId)).isInstanceOf(NotFoundException.class);
    }
}
