package com.evenly.group.application.port.out;

import com.evenly.group.domain.Group;
import java.util.UUID;

public interface SaveGroupPort {

    Group save(Group group);

    void deleteById(UUID id);
}
