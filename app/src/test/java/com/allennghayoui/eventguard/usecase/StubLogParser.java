package com.allennghayoui.eventguard.usecase;

import java.time.Instant;
import java.util.Map;

import com.allennghayoui.eventguard.domain.LogEvent;
import com.allennghayoui.eventguard.domain.Severity;
import com.allennghayoui.eventguard.usecase.port.LogParser;

public class StubLogParser implements LogParser {
    
    @Override
    public LogEvent parse(String rawLine, String source) {
        return LogEvent.create(
            Instant.parse("2025-01-01T00:00:00Z"),
            source,
            Severity.INFO,
            rawLine,
            Map.of()
        );
    }
}
