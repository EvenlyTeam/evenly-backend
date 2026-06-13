package com.evenly.group.application.port.in;

import com.evenly.group.application.dto.CreateGroupCommand;
import com.evenly.group.application.dto.GroupInfo;

public interface CreateGroupUseCase {

    GroupInfo createGroup(CreateGroupCommand command);
}
