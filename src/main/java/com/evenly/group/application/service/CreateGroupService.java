package com.evenly.group.application.service;

import com.evenly.common.domain.ConflictException;
import com.evenly.common.domain.NotFoundException;
import com.evenly.group.application.dto.CreateGroupCommand;
import com.evenly.group.application.dto.GroupDetail;
import com.evenly.group.application.port.in.CreateGroupUseCase;
import com.evenly.group.application.port.out.SaveGroupPort;
import com.evenly.group.application.port.out.SaveParticipantPort;
import com.evenly.group.domain.Group;
import com.evenly.group.domain.Participant;
import com.evenly.user.application.port.out.LoadUserPort;
import com.evenly.user.domain.User;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class CreateGroupService implements CreateGroupUseCase {

    private final SaveGroupPort saveGroupPort;
    private final SaveParticipantPort saveParticipantPort;
    private final LoadUserPort loadUserPort;

    CreateGroupService(
            SaveGroupPort saveGroupPort, SaveParticipantPort saveParticipantPort, LoadUserPort loadUserPort) {
        this.saveGroupPort = saveGroupPort;
        this.saveParticipantPort = saveParticipantPort;
        this.loadUserPort = loadUserPort;
    }

    @Override
    public GroupDetail createGroup(CreateGroupCommand command) {
        List<String> names = command.participantNames();
        if (names.stream().distinct().count() != names.size()) {
            throw new ConflictException("참여자 이름이 중복되었습니다");
        }

        User creator = loadUserPort
                .findById(command.ownerId())
                .orElseThrow(() -> new NotFoundException("User", command.ownerId()));

        // createdAt 은 영속 시점에 JPA Auditing 이 채운 뒤 save() 결과에 반영된다.
        Group savedGroup = saveGroupPort.save(Group.create(command.name(), command.ownerId()));

        // 생성자는 본인 계정과 연결된 참여자로 항상 포함한다("내 잔액"용).
        // 요청한 이름 중 생성자 닉네임과 같은 항목이 있으면 그 참여자를 연결한다.
        String myName = creator.getDisplayName();
        List<Participant> participants = new ArrayList<>();
        if (!names.contains(myName)) {
            participants.add(Participant.createForUser(savedGroup.getId(), command.ownerId(), myName));
        }
        for (String name : names) {
            if (name.equals(myName)) {
                participants.add(Participant.createForUser(savedGroup.getId(), command.ownerId(), name));
            } else {
                participants.add(Participant.create(savedGroup.getId(), name));
            }
        }

        return GroupDetail.from(savedGroup, saveParticipantPort.saveAll(participants));
    }
}
