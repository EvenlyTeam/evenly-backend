package com.evenly.group.application.port.in;

import com.evenly.group.application.dto.SettlementInfo;
import java.util.UUID;

public interface GetSettlementUseCase {

    SettlementInfo getSettlement(UUID groupId);
}
