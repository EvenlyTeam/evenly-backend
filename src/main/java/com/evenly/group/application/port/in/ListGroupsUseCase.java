package com.evenly.group.application.port.in;

import com.evenly.group.application.dto.GroupSummary;
import java.util.List;
import java.util.UUID;

public interface ListGroupsUseCase {

    List<GroupSummary> listGroups(UUID ownerId);
}
