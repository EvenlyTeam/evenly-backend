package com.evenly.group.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.evenly.common.domain.NotFoundException;
import com.evenly.group.application.dto.GroupDetail;
import com.evenly.group.application.port.out.LoadGroupPort;
import com.evenly.group.application.port.out.LoadParticipantPort;
import com.evenly.group.application.port.out.SaveGroupPort;
import com.evenly.group.domain.Group;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateGroupServiceTest {

    @Mock
    LoadGroupPort loadGroupPort;

    @Mock
    SaveGroupPort saveGroupPort;

    @Mock
    LoadParticipantPort loadParticipantPort;

    @InjectMocks
    UpdateGroupService service;

    @Test
    void 모임_이름을_변경한다() {
        UUID id = UUID.randomUUID();
        when(loadGroupPort.findById(id)).thenReturn(Optional.of(new Group(id, "옛이름", UUID.randomUUID(), null, null)));
        when(saveGroupPort.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(loadParticipantPort.findByGroupId(id)).thenReturn(List.of());

        GroupDetail result = service.rename(id, "새이름");

        assertThat(result.name()).isEqualTo("새이름");
    }

    @Test
    void 없는_모임이면_NotFound() {
        UUID id = UUID.randomUUID();
        when(loadGroupPort.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.rename(id, "x")).isInstanceOf(NotFoundException.class);
    }
}
