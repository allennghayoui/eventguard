package com.allennghayoui.eventguard.infrastructure.web;

public class InputLimits {
    private InputLimits() {}

    /**
     * Max accepted raw log line length.
     * RFC 3164 caps at 1024 bytes; RFC 5424 recommends receivers support
     * at least 2048 bytes with no hard upper limit. We cap at 8KB to allow
     * headroom for verbose RFC 5424 messages with structured data, while
     * bounding worst-case memory pressure from any single line.
     */
    public static final int MAX_RAW_LINE_LENGTH = 8192;

    // Max source identifier length.
    public static final int MAX_SOURCE_LENGTH = 64;

    // Pattern for valid source identifiers: alphanumeric, dash, underscore, dot.
    public static final String SOURCE_PATTERN = "^[a-zA-Z0-9._-]+$";

    // Max time-range span for a query. 100 days. Longer queries must paginate or refine.
    public static final long MAX_TIME_RANGE_DAYS = 100;

    // Default page size if not specified.
    public static final int DEFAULT_PAGE_SIZE = 50;

    // Max page size a caller can request.
    public static final int MAX_PAGE_SIZE = 500;
}
