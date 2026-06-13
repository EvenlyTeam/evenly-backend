package com.evenly.group.application.service;

import com.evenly.common.domain.NotFoundException;
import com.evenly.group.application.dto.GroupInfo;
import com.evenly.group.application.port.in.GetGroupUseCase;
import com.evenly.group.application.port.out.LoadGroupPort;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
class GetGroupService implements GetGroupUseCase {

    private final LoadGroupPort loadGroupPort;

    GetGroupService(LoadGroupPort loadGroupPort) {
        this.loadGroupPort = loadGroupPort;
    }

    @Override
    public GroupInfo getGroup(UUID id) {
        return loadGroupPort.findById(id).map(GroupInfo::from).orElseThrow(() -> new NotFoundException("Group", id));
    }
}
