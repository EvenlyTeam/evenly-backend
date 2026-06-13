package com.evenly.group.application.port.in;

import com.evenly.group.application.dto.GroupInfo;
import java.util.UUID;

public interface GetGroupUseCase {

    GroupInfo getGroup(UUID id);
}
