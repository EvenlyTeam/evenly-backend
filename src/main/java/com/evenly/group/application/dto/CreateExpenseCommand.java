package com.evenly.group.application.dto;

import java.util.List;
import java.util.UUID;

public record CreateExpenseCommand(
        UUID groupId, UUID payerId, String description, long amount, List<UUID> shareParticipantIds) {

    public CreateExpenseCommand {
        shareParticipantIds = shareParticipantIds == null ? List.of() : List.copyOf(shareParticipantIds);
    }
}
