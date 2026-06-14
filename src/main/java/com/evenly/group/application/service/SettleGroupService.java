package com.evenly.group.application.service;

import com.evenly.common.domain.NotFoundException;
import com.evenly.group.application.port.in.SettleGroupUseCase;
import com.evenly.group.application.port.out.LoadGroupPort;
import com.evenly.group.application.port.out.SaveGroupPort;
import com.evenly.group.domain.Group;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class SettleGroupService implements SettleGroupUseCase {

    private final LoadGroupPort loadGroupPort;
    private final SaveGroupPort saveGroupPort;

    SettleGroupService(LoadGroupPort loadGroupPort, SaveGroupPort saveGroupPort) {
        this.loadGroupPort = loadGroupPort;
        this.saveGroupPort = saveGroupPort;
    }

    @Override
    public void settle(UUID groupId) {
        Group group = loadGroupPort.findById(groupId).orElseThrow(() -> new NotFoundException("Group", groupId));
        saveGroupPort.save(group.markSettled());
    }

    @Override
    public void reopen(UUID groupId) {
        Group group = loadGroupPort.findById(groupId).orElseThrow(() -> new NotFoundException("Group", groupId));
        saveGroupPort.save(group.reopen());
    }
}
