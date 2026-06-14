package com.evenly.group.application.service;

import com.evenly.common.domain.ConflictException;
import com.evenly.group.application.dto.CreateGroupCommand;
import com.evenly.group.application.dto.GroupDetail;
import com.evenly.group.application.port.in.CreateGroupUseCase;
import com.evenly.group.application.port.out.SaveGroupPort;
import com.evenly.group.application.port.out.SaveParticipantPort;
import com.evenly.group.domain.Group;
import com.evenly.group.domain.Participant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class CreateGroupService implements CreateGroupUseCase {

    private final SaveGroupPort saveGroupPort;
    private final SaveParticipantPort saveParticipantPort;

    CreateGroupService(SaveGroupPort saveGroupPort, SaveParticipantPort saveParticipantPort) {
        this.saveGroupPort = saveGroupPort;
        this.saveParticipantPort = saveParticipantPort;
    }

    @Override
    public GroupDetail createGroup(CreateGroupCommand command) {
        List<String> names = command.participantNames();
        if (names.stream().distinct().count() != names.size()) {
            throw new ConflictException("참여자 이름이 중복되었습니다");
        }

        // createdAt 은 영속 시점에 JPA Auditing 이 채운 뒤 save() 결과에 반영된다.
        Group group = new Group(UUID.randomUUID(), command.name(), command.ownerId(), null, null);
        Group savedGroup = saveGroupPort.save(group);

        List<Participant> participants = names.stream()
                .map(name -> new Participant(UUID.randomUUID(), savedGroup.getId(), null, name))
                .toList();
        List<Participant> savedParticipants =
                participants.isEmpty() ? List.of() : saveParticipantPort.saveAll(participants);

        return GroupDetail.from(savedGroup, savedParticipants);
    }
}
