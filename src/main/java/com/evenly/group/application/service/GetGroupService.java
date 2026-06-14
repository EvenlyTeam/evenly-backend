package com.evenly.group.application.service;

import com.evenly.common.domain.NotFoundException;
import com.evenly.group.application.dto.GroupDetail;
import com.evenly.group.application.port.in.GetGroupUseCase;
import com.evenly.group.application.port.out.LoadGroupPort;
import com.evenly.group.application.port.out.LoadParticipantPort;
import com.evenly.group.domain.Group;
import com.evenly.group.domain.Participant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
class GetGroupService implements GetGroupUseCase {

    private final LoadGroupPort loadGroupPort;
    private final LoadParticipantPort loadParticipantPort;

    GetGroupService(LoadGroupPort loadGroupPort, LoadParticipantPort loadParticipantPort) {
        this.loadGroupPort = loadGroupPort;
        this.loadParticipantPort = loadParticipantPort;
    }

    @Override
    public GroupDetail getGroup(UUID id) {
        Group group = loadGroupPort.findById(id).orElseThrow(() -> new NotFoundException("Group", id));
        List<Participant> participants = loadParticipantPort.findByGroupId(id);
        return GroupDetail.from(group, participants);
    }
}
