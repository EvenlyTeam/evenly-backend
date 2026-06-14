package com.evenly.group.application.dto;

import java.util.List;
import java.util.UUID;

public record CreateGroupCommand(String name, UUID ownerId, List<String> participantNames) {

    public CreateGroupCommand {
        participantNames = participantNames == null ? List.of() : List.copyOf(participantNames);
    }
}
