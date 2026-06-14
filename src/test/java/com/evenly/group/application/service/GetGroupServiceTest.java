package com.evenly.group.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.evenly.common.domain.NotFoundException;
import com.evenly.group.application.dto.GroupDetail;
import com.evenly.group.application.port.out.LoadGroupPort;
import com.evenly.group.application.port.out.LoadParticipantPort;
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
class GetGroupServiceTest {

    @Mock
    LoadGroupPort loadGroupPort;

    @Mock
    LoadParticipantPort loadParticipantPort;

    @InjectMocks
    GetGroupService service;

    @Test
    void 모임과_참여자를_반환한다() {
        UUID groupId = UUID.randomUUID();
        Group group = new Group(groupId, "강릉", UUID.randomUUID(), null, null);
        when(loadGroupPort.findById(groupId)).thenReturn(Optional.of(group));
        when(loadParticipantPort.findByGroupId(groupId))
                .thenReturn(List.of(new Participant(UUID.randomUUID(), groupId, null, "준호")));

        GroupDetail result = service.getGroup(groupId);

        assertThat(result.id()).isEqualTo(groupId);
        assertThat(result.participants()).hasSize(1);
    }

    @Test
    void 없는_모임이면_NotFound() {
        UUID groupId = UUID.randomUUID();
        when(loadGroupPort.findById(groupId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getGroup(groupId)).isInstanceOf(NotFoundException.class);
    }
}
