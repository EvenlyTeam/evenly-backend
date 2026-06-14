package com.evenly.group.application.service;

import com.evenly.common.domain.NotFoundException;
import com.evenly.group.application.dto.GroupDetail;
import com.evenly.group.application.port.in.UpdateGroupUseCase;
import com.evenly.group.application.port.out.LoadGroupPort;
import com.evenly.group.application.port.out.LoadParticipantPort;
import com.evenly.group.application.port.out.SaveGroupPort;
import com.evenly.group.domain.Group;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class UpdateGroupService implements UpdateGroupUseCase {

    private final LoadGroupPort loadGroupPort;
    private final SaveGroupPort saveGroupPort;
    private final LoadParticipantPort loadParticipantPort;

    UpdateGroupService(
            LoadGroupPort loadGroupPort, SaveGroupPort saveGroupPort, LoadParticipantPort loadParticipantPort) {
        this.loadGroupPort = loadGroupPort;
        this.saveGroupPort = saveGroupPort;
        this.loadParticipantPort = loadParticipantPort;
    }

    @Override
    public GroupDetail rename(UUID groupId, String name) {
        Group group = loadGroupPort.findById(groupId).orElseThrow(() -> new NotFoundException("Group", groupId));
        Group renamed = saveGroupPort.save(group.rename(name));
        return GroupDetail.from(renamed, loadParticipantPort.findByGroupId(groupId));
    }
}
