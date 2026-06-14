package com.evenly.group.application;

import com.evenly.common.domain.ForbiddenException;
import com.evenly.common.domain.NotFoundException;
import com.evenly.group.application.port.out.LoadGroupPort;
import com.evenly.group.domain.Group;
import java.util.UUID;
import org.springframework.stereotype.Component;

/** 모임 접근 권한 가드: 존재 검사(404) + 소유자 검사(403)를 한 곳에서 수행한다. */
@Component
public class GroupAccessGuard {

    private final LoadGroupPort loadGroupPort;

    GroupAccessGuard(LoadGroupPort loadGroupPort) {
        this.loadGroupPort = loadGroupPort;
    }

    /** 모임을 조회하고, 요청자가 소유자가 아니면 거부한다. */
    public Group requireOwner(UUID groupId, UUID requesterId) {
        Group group = loadGroupPort.findById(groupId).orElseThrow(() -> new NotFoundException("Group", groupId));
        if (!group.isOwnedBy(requesterId)) {
            throw new ForbiddenException("해당 모임에 접근할 권한이 없습니다");
        }
        return group;
    }
}
