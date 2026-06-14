package com.evenly.group.application.service;

import com.evenly.common.domain.NotFoundException;
import com.evenly.group.application.port.in.RemoveParticipantUseCase;
import com.evenly.group.application.port.out.LoadParticipantPort;
import com.evenly.group.application.port.out.SaveParticipantPort;
import com.evenly.group.domain.Participant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class RemoveParticipantService implements RemoveParticipantUseCase {

    private final LoadParticipantPort loadParticipantPort;
    private final SaveParticipantPort saveParticipantPort;

    RemoveParticipantService(LoadParticipantPort loadParticipantPort, SaveParticipantPort saveParticipantPort) {
        this.loadParticipantPort = loadParticipantPort;
        this.saveParticipantPort = saveParticipantPort;
    }

    @Override
    public void removeParticipant(UUID groupId, UUID participantId) {
        Participant participant = loadParticipantPort
                .findById(participantId)
                .orElseThrow(() -> new NotFoundException("Participant", participantId));
        if (!participant.belongsTo(groupId)) {
            throw new NotFoundException("Participant", participantId);
        }
        saveParticipantPort.deleteById(participantId);
    }
}
