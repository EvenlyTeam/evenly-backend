package com.evenly.group.application.service;

import com.evenly.group.application.dto.GroupSummary;
import com.evenly.group.application.port.in.ListGroupsUseCase;
import com.evenly.group.application.port.out.LoadGroupPort;
import com.evenly.group.application.port.out.LoadParticipantPort;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
class ListGroupsService implements ListGroupsUseCase {

    private final LoadGroupPort loadGroupPort;
    private final LoadParticipantPort loadParticipantPort;

    ListGroupsService(LoadGroupPort loadGroupPort, LoadParticipantPort loadParticipantPort) {
        this.loadGroupPort = loadGroupPort;
        this.loadParticipantPort = loadParticipantPort;
    }

    @Override
    public List<GroupSummary> listGroups(UUID ownerId) {
        return loadGroupPort.findByOwnerId(ownerId).stream()
                .map(group -> new GroupSummary(
                        group.getId(),
                        group.getName().value(),
                        loadParticipantPort.countByGroupId(group.getId()),
                        group.getCreatedAt()))
                .toList();
    }
}
