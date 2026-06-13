package com.evenly.group.application.service;

import com.evenly.group.application.dto.CreateGroupCommand;
import com.evenly.group.application.dto.GroupInfo;
import com.evenly.group.application.port.in.CreateGroupUseCase;
import com.evenly.group.application.port.out.SaveGroupPort;
import com.evenly.group.domain.Group;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class CreateGroupService implements CreateGroupUseCase {

    private final SaveGroupPort saveGroupPort;

    CreateGroupService(SaveGroupPort saveGroupPort) {
        this.saveGroupPort = saveGroupPort;
    }

    @Override
    public GroupInfo createGroup(CreateGroupCommand command) {
        // createdAt 은 영속 시점에 JPA Auditing 이 채운 뒤 save() 결과에 반영된다.
        Group group = new Group(UUID.randomUUID(), command.name(), command.ownerId(), null, null);
        return GroupInfo.from(saveGroupPort.save(group));
    }
}
