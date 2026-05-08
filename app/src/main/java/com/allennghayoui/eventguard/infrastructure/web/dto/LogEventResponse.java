package com.allennghayoui.eventguard.infrastructure.web.dto;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import com.allennghayoui.eventguard.domain.LogEvent;
import com.allennghayoui.eventguard.domain.Severity;

public record LogEventResponse(
    UUID id,
    Instant timestamp,
    String source,
    Severity severity,
    String message,
    Map<String, String> fields
) {
    public static LogEventResponse fromDomain(LogEvent event) {
        return new LogEventResponse(
            event.id(),
            event.timestamp(),
            event.source(),
            event.severity(),
            event.message(),
            event.fields()
        );
    }
}


