package com.evenly.group.application.dto;

import java.util.List;
import java.util.UUID;

public record UpdateExpenseCommand(
        UUID groupId, UUID expenseId, UUID payerId, String description, long amount, List<UUID> shareParticipantIds) {

    public UpdateExpenseCommand {
        shareParticipantIds = shareParticipantIds == null ? List.of() : List.copyOf(shareParticipantIds);
    }
}
