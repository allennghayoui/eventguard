package com.allennghayoui.eventguard.usecase.port;

public record PaginatedRequest(int page, int size) {
    public PaginatedRequest {
        if (page < 0) throw new IllegalArgumentException("page must be >= 0");
        if (size <= 0) throw new IllegalArgumentException("size must be > 0");
        if (size > 1000) throw new IllegalArgumentException("size must be <= 1000");
    }
}
