package com.evenly.group.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.evenly.group.application.dto.GroupSummary;
import com.evenly.group.application.port.out.LoadGroupPort;
import com.evenly.group.application.port.out.LoadParticipantPort;
import com.evenly.group.domain.Group;
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

    @InjectMocks
    ListGroupsService service;

    @Test
    void 소유한_모임을_참여자수와_함께_반환한다() {
        UUID owner = UUID.randomUUID();
        Group group = new Group(UUID.randomUUID(), "강릉", owner, null, null);
        when(loadGroupPort.findByOwnerId(owner)).thenReturn(List.of(group));
        when(loadParticipantPort.countByGroupId(group.getId())).thenReturn(4);

        List<GroupSummary> result = service.listGroups(owner);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).participantCount()).isEqualTo(4);
        assertThat(result.get(0).name()).isEqualTo("강릉");
    }

    @Test
    void 소유한_모임이_없으면_빈_목록() {
        UUID owner = UUID.randomUUID();
        when(loadGroupPort.findByOwnerId(owner)).thenReturn(List.of());

        assertThat(service.listGroups(owner)).isEmpty();
    }
}
