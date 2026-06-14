package com.evenly.group.application.port.out;

import com.evenly.group.domain.Participant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoadParticipantPort {

    List<Participant> findByGroupId(UUID groupId);

    Optional<Participant> findById(UUID id);

    boolean existsByGroupIdAndName(UUID groupId, String name);

    int countByGroupId(UUID groupId);
}
