package com.evenly.group.application.port.in;

import java.util.UUID;

public interface DeleteGroupUseCase {

    /** 모임 삭제. 참여자/지출/분담은 DB cascade 로 함께 삭제된다. */
    void deleteGroup(UUID groupId);
}
