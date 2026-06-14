package com.evenly.group.application.port.in;

import com.evenly.group.application.dto.SharedSettlementInfo;

public interface GetSharedSettlementUseCase {

    SharedSettlementInfo getSharedSettlement(String shareToken);
}
