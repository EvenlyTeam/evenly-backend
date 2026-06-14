package com.evenly.group.application.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.evenly.common.domain.NotFoundException;
import com.evenly.group.application.port.out.LoadParticipantPort;
import com.evenly.group.application.port.out.SaveParticipantPort;
import com.evenly.group.domain.Participant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RemoveParticipantServiceTest {

    @Mock
    LoadParticipantPort loadParticipantPort;

    @Mock
    SaveParticipantPort saveParticipantPort;

    @InjectMocks
    RemoveParticipantService service;

    @Test
    void 참여자를_삭제한다() {
        UUID groupId = UUID.randomUUID();
        UUID participantId = UUID.randomUUID();
        when(loadParticipantPort.findById(participantId))
                .thenReturn(Optional.of(new Participant(participantId, groupId, null, "준호")));

        service.removeParticipant(groupId, participantId);

        verify(saveParticipantPort).deleteById(participantId);
    }

    @Test
    void 없는_참여자면_NotFound() {
        UUID participantId = UUID.randomUUID();
        when(loadParticipantPort.findById(participantId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.removeParticipant(UUID.randomUUID(), participantId))
                .isInstanceOf(NotFoundException.class);
        verify(saveParticipantPort, never()).deleteById(participantId);
    }

    @Test
    void 다른_모임의_참여자면_NotFound() {
        UUID participantId = UUID.randomUUID();
        when(loadParticipantPort.findById(participantId))
                .thenReturn(Optional.of(new Participant(participantId, UUID.randomUUID(), null, "준호")));

        assertThatThrownBy(() -> service.removeParticipant(UUID.randomUUID(), participantId))
                .isInstanceOf(NotFoundException.class);
        verify(saveParticipantPort, never()).deleteById(participantId);
    }
}
