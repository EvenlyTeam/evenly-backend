package com.evenly.group.application.service;

import com.evenly.common.domain.NotFoundException;
import com.evenly.group.application.dto.SettlementInfo;
import com.evenly.group.application.dto.SettlementInfo.BalanceInfo;
import com.evenly.group.application.dto.SettlementInfo.TransferInfo;
import com.evenly.group.application.port.in.GetSettlementUseCase;
import com.evenly.group.application.port.out.LoadExpensePort;
import com.evenly.group.application.port.out.LoadGroupPort;
import com.evenly.group.application.port.out.LoadParticipantPort;
import com.evenly.group.domain.Participant;
import com.evenly.settlement.domain.ExpenseLine;
import com.evenly.settlement.domain.Settlement;
import com.evenly.settlement.domain.SettlementCalculator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
class GetSettlementService implements GetSettlementUseCase {

    private final LoadGroupPort loadGroupPort;
    private final LoadParticipantPort loadParticipantPort;
    private final LoadExpensePort loadExpensePort;

    GetSettlementService(
            LoadGroupPort loadGroupPort, LoadParticipantPort loadParticipantPort, LoadExpensePort loadExpensePort) {
        this.loadGroupPort = loadGroupPort;
        this.loadParticipantPort = loadParticipantPort;
        this.loadExpensePort = loadExpensePort;
    }

    @Override
    public SettlementInfo getSettlement(UUID groupId) {
        loadGroupPort.findById(groupId).orElseThrow(() -> new NotFoundException("Group", groupId));

        List<Participant> participants = loadParticipantPort.findByGroupId(groupId);
        Map<UUID, String> nameById = participants.stream()
                .collect(Collectors.toMap(Participant::getId, p -> p.getName().value()));
        List<UUID> participantIds =
                participants.stream().map(Participant::getId).toList();

        List<ExpenseLine> lines = loadExpensePort.findByGroupId(groupId).stream()
                .map(e -> new ExpenseLine(e.getPayerId(), e.getAmount(), e.getShareParticipantIds()))
                .toList();

        Settlement settlement = SettlementCalculator.calculate(participantIds, lines);

        List<BalanceInfo> balances = settlement.balances().stream()
                .map(b -> new BalanceInfo(b.participantId(), nameById.get(b.participantId()), b.net()))
                .toList();
        List<TransferInfo> transfers = settlement.transfers().stream()
                .map(t -> new TransferInfo(
                        t.fromParticipantId(),
                        nameById.get(t.fromParticipantId()),
                        t.toParticipantId(),
                        nameById.get(t.toParticipantId()),
                        t.amount()))
                .toList();

        return new SettlementInfo(settlement.total(), balances, transfers);
    }
}
