package com.evenly.common.application;

import java.util.List;

public record PagedResult<T>(List<T> items, int page, int size, long totalElements, int totalPages) {}
