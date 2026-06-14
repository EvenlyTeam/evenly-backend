package com.evenly.group.application.port.in;

import java.util.UUID;

public interface SettleGroupUseCase {

    /** 정산 완료로 표시. */
    void settle(UUID groupId);

    /** 정산 완료 해제(되돌리기). */
    void reopen(UUID groupId);
}
