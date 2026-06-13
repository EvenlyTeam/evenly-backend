package com.evenly.group.application.port.out;

import com.evenly.group.domain.Group;

public interface SaveGroupPort {

    Group save(Group group);
}
