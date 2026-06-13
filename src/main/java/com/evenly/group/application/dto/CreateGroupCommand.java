package com.evenly.group.application.dto;

import java.util.UUID;

public record CreateGroupCommand(String name, UUID ownerId) {}
