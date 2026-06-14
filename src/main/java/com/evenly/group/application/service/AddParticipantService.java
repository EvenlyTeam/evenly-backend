package com.evenly.group.application.service;

import com.evenly.common.domain.ConflictException;
import com.evenly.common.domain.NotFoundException;
import com.evenly.group.application.dto.AddParticipantCommand;
import com.evenly.group.application.dto.ParticipantInfo;
import com.evenly.group.application.port.in.AddParticipantUseCase;
import com.evenly.group.application.port.out.LoadGroupPort;
import com.evenly.group.application.port.out.LoadParticipantPort;
import com.evenly.group.application.port.out.SaveParticipantPort;
import com.evenly.group.domain.Group;
import com.evenly.group.domain.Participant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class AddParticipantService implements AddParticipantUseCase {

    private final LoadGroupPort loadGroupPort;
    private final LoadParticipantPort loadParticipantPort;
    private final SaveParticipantPort saveParticipantPort;

    AddParticipantService(
            LoadGroupPort loadGroupPort,
            LoadParticipantPort loadParticipantPort,
            SaveParticipantPort saveParticipantPort) {
        this.loadGroupPort = loadGroupPort;
        this.loadParticipantPort = loadParticipantPort;
        this.saveParticipantPort = saveParticipantPort;
    }

    @Override
    public ParticipantInfo addParticipant(AddParticipantCommand command) {
        Group group = loadGroupPort
                .findById(command.groupId())
                .orElseThrow(() -> new NotFoundException("Group", command.groupId()));
        if (loadParticipantPort.existsByGroupIdAndName(group.getId(), command.name())) {
            throw new ConflictException("이미 존재하는 참여자 이름입니다: " + command.name());
        }
        Participant participant = new Participant(UUID.randomUUID(), group.getId(), null, command.name());
        return ParticipantInfo.from(saveParticipantPort.save(participant));
    }
}
