package com.evenly.user.application.dto;

public record SignupCommand(String email, String displayName, String rawPassword) {}
