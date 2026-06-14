package com.evenly.group.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "공유 링크 정보")
public record ShareLinkInfo(@Schema(description = "공유 토큰", example = "Xy3k9aQ2...") String shareToken) {}
