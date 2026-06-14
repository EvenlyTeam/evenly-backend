package com.evenly.group.application.port.out;

import com.evenly.group.domain.Group;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoadGroupPort {

    Optional<Group> findById(UUID id);

    List<Group> findByOwnerId(UUID ownerId);
}
