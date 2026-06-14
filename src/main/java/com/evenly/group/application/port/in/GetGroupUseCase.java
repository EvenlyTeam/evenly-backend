package com.evenly.group.application.port.in;

import com.evenly.group.application.dto.GroupDetail;
import java.util.UUID;

public interface GetGroupUseCase {

    GroupDetail getGroup(UUID id);
}
