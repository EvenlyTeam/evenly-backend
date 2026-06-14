package com.evenly.group.application.service;

import com.evenly.group.application.dto.GroupSummary;
import com.evenly.group.application.port.in.ListGroupsUseCase;
import com.evenly.group.application.port.out.LoadExpensePort;
import com.evenly.group.application.port.out.LoadGroupPort;
import com.evenly.group.application.port.out.LoadParticipantPort;
import com.evenly.group.domain.Participant;
import com.evenly.settlement.domain.Balance;
import com.evenly.settlement.domain.ExpenseLine;
import com.evenly.settlement.domain.Settlement;
import com.evenly.settlement.domain.SettlementCalculator;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
class ListGroupsService implements ListGroupsUseCase {

    private final LoadGroupPort loadGroupPort;
    private final LoadParticipantPort loadParticipantPort;
    private final LoadExpensePort loadExpensePort;

    ListGroupsService(
            LoadGroupPort loadGroupPort, LoadParticipantPort loadParticipantPort, LoadExpensePort loadExpensePort) {
        this.loadGroupPort = loadGroupPort;
        this.loadParticipantPort = loadParticipantPort;
        this.loadExpensePort = loadExpensePort;
    }

    @Override
    public List<GroupSummary> listGroups(UUID ownerId) {
        return loadGroupPort.findByOwnerId(ownerId).stream()
                .map(group -> {
                    List<Participant> participants = loadParticipantPort.findByGroupId(group.getId());
                    return new GroupSummary(
                            group.getId(),
                            group.getName().value(),
                            participants.size(),
                            myBalance(group.getId(), ownerId, participants),
                            group.isSettled(),
                            group.getCreatedAt());
                })
                .toList();
    }

    /** 요청자와 연결된 참여자의 순잔액. 연결된 참여자가 없으면 null. */
    private Long myBalance(UUID groupId, UUID userId, List<Participant> participants) {
        UUID myParticipantId = participants.stream()
                .filter(p -> userId.equals(p.getUserId()))
                .map(Participant::getId)
                .findFirst()
                .orElse(null);
        if (myParticipantId == null) {
            return null;
        }
        List<UUID> participantIds =
                participants.stream().map(Participant::getId).toList();
        List<ExpenseLine> lines = loadExpensePort.findByGroupId(groupId).stream()
                .map(e -> new ExpenseLine(e.getPayerId(), e.getAmount(), e.getShareParticipantIds()))
                .toList();
        Settlement settlement = SettlementCalculator.calculate(participantIds, lines);
        return settlement.balances().stream()
                .filter(b -> b.participantId().equals(myParticipantId))
                .map(Balance::net)
                .findFirst()
                .orElse(0L);
    }
}
