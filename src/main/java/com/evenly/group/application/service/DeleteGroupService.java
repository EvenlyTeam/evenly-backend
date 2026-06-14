package com.evenly.group.application.service;

import com.evenly.group.application.port.in.DeleteGroupUseCase;
import com.evenly.group.application.port.out.SaveGroupPort;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class DeleteGroupService implements DeleteGroupUseCase {

    private final SaveGroupPort saveGroupPort;

    DeleteGroupService(SaveGroupPort saveGroupPort) {
        this.saveGroupPort = saveGroupPort;
    }

    @Override
    public void deleteGroup(UUID groupId) {
        saveGroupPort.deleteById(groupId);
    }
}
