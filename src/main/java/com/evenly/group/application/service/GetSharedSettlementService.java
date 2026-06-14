package com.evenly.group.application.service;

import com.evenly.common.domain.NotFoundException;
import com.evenly.group.application.dto.SettlementInfo;
import com.evenly.group.application.dto.SharedSettlementInfo;
import com.evenly.group.application.port.in.GetSettlementUseCase;
import com.evenly.group.application.port.in.GetSharedSettlementUseCase;
import com.evenly.group.application.port.out.LoadGroupPort;
import com.evenly.group.domain.Group;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
class GetSharedSettlementService implements GetSharedSettlementUseCase {

    private final LoadGroupPort loadGroupPort;
    private final GetSettlementUseCase getSettlementUseCase;

    GetSharedSettlementService(LoadGroupPort loadGroupPort, GetSettlementUseCase getSettlementUseCase) {
        this.loadGroupPort = loadGroupPort;
        this.getSettlementUseCase = getSettlementUseCase;
    }

    @Override
    public SharedSettlementInfo getSharedSettlement(String shareToken) {
        Group group = loadGroupPort
                .findByShareToken(shareToken)
                .orElseThrow(() -> new NotFoundException("SharedGroup", shareToken));
        SettlementInfo settlement = getSettlementUseCase.getSettlement(group.getId());
        return new SharedSettlementInfo(group.getId(), group.getName().value(), settlement);
    }
}
