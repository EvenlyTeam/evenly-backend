package com.evenly.group.application.dto;

import java.util.UUID;

public record AddParticipantCommand(UUID groupId, String name) {}
