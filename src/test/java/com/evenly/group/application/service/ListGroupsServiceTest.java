package com.evenly.group.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.evenly.group.application.dto.GroupSummary;
import com.evenly.group.application.port.out.LoadExpensePort;
import com.evenly.group.application.port.out.LoadGroupPort;
import com.evenly.group.application.port.out.LoadParticipantPort;
import com.evenly.group.domain.Expense;
import com.evenly.group.domain.Group;
import com.evenly.group.domain.Participant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListGroupsServiceTest {

    @Mock
    LoadGroupPort loadGroupPort;

    @Mock
    LoadParticipantPort loadParticipantPort;

    @Mock
    LoadExpensePort loadExpensePort;

    @InjectMocks
    ListGroupsService service;

    @Test
    void 연결된_참여자가_있으면_내_순잔액을_계산한다() {
        UUID owner = UUID.randomUUID();
        Group group = new Group(UUID.randomUUID(), "강릉", owner, null, null);
        UUID myPid = UUID.randomUUID();
        UUID otherPid = UUID.randomUUID();
        when(loadGroupPort.findByOwnerId(owner)).thenReturn(List.of(group));
        when(loadParticipantPort.findByGroupId(group.getId()))
                .thenReturn(List.of(
                        new Participant(myPid, group.getId(), owner, "오너"),
                        new Participant(otherPid, group.getId(), null, "민지")));
        // 오너가 10,000 결제, 2명 분담(각 5,000) → 오너 +5,000
        when(loadExpensePort.findByGroupId(group.getId()))
                .thenReturn(List.of(
                        new Expense(UUID.randomUUID(), group.getId(), myPid, "저녁", 10000, List.of(myPid, otherPid))));

        List<GroupSummary> result = service.listGroups(owner);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).participantCount()).isEqualTo(2);
        assertThat(result.get(0).myBalance()).isEqualTo(5000L);
    }

    @Test
    void 연결된_참여자가_없으면_myBalance는_null() {
        UUID owner = UUID.randomUUID();
        Group group = new Group(UUID.randomUUID(), "강릉", owner, null, null);
        when(loadGroupPort.findByOwnerId(owner)).thenReturn(List.of(group));
        when(loadParticipantPort.findByGroupId(group.getId()))
                .thenReturn(List.of(new Participant(UUID.randomUUID(), group.getId(), null, "민지")));

        List<GroupSummary> result = service.listGroups(owner);

        assertThat(result.get(0).myBalance()).isNull();
    }

    @Test
    void 소유한_모임이_없으면_빈_목록() {
        UUID owner = UUID.randomUUID();
        when(loadGroupPort.findByOwnerId(owner)).thenReturn(List.of());

        assertThat(service.listGroups(owner)).isEmpty();
    }
}
