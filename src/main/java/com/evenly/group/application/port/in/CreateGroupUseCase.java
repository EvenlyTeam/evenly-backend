package com.evenly.group.application.port.in;

import com.evenly.group.application.dto.CreateGroupCommand;
import com.evenly.group.application.dto.GroupDetail;

public interface CreateGroupUseCase {

    GroupDetail createGroup(CreateGroupCommand command);
}
