package com.evenly.group.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.evenly.common.domain.ConflictException;
import com.evenly.common.domain.NotFoundException;
import com.evenly.group.application.dto.AddParticipantCommand;
import com.evenly.group.application.dto.ParticipantInfo;
import com.evenly.group.application.port.out.LoadGroupPort;
import com.evenly.group.application.port.out.LoadParticipantPort;
import com.evenly.group.application.port.out.SaveParticipantPort;
import com.evenly.group.domain.Group;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AddParticipantServiceTest {

    @Mock
    LoadGroupPort loadGroupPort;

    @Mock
    LoadParticipantPort loadParticipantPort;

    @Mock
    SaveParticipantPort saveParticipantPort;

    @InjectMocks
    AddParticipantService service;

    @Test
    void 참여자를_추가한다() {
        UUID groupId = UUID.randomUUID();
        when(loadGroupPort.findById(groupId))
                .thenReturn(Optional.of(new Group(groupId, "g", UUID.randomUUID(), null, null)));
        when(loadParticipantPort.existsByGroupIdAndName(groupId, "지호")).thenReturn(false);
        when(saveParticipantPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ParticipantInfo result = service.addParticipant(new AddParticipantCommand(groupId, "지호"));

        assertThat(result.name()).isEqualTo("지호");
    }

    @Test
    void 없는_모임이면_NotFound() {
        UUID groupId = UUID.randomUUID();
        when(loadGroupPort.findById(groupId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.addParticipant(new AddParticipantCommand(groupId, "지호")))
                .isInstanceOf(NotFoundException.class);
        verify(saveParticipantPort, never()).save(any());
    }

    @Test
    void 이름이_중복이면_충돌() {
        UUID groupId = UUID.randomUUID();
        when(loadGroupPort.findById(groupId))
                .thenReturn(Optional.of(new Group(groupId, "g", UUID.randomUUID(), null, null)));
        when(loadParticipantPort.existsByGroupIdAndName(groupId, "준호")).thenReturn(true);

        assertThatThrownBy(() -> service.addParticipant(new AddParticipantCommand(groupId, "준호")))
                .isInstanceOf(ConflictException.class);
        verify(saveParticipantPort, never()).save(any());
    }
}
