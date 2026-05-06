package com.allennghayoui.eventguard.domain;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class LogEvent {
    private final UUID id;
    private final Instant timestamp;
    private final String source;
    private final Severity severity;
    private final String message;
    private final Map<String, String> fields;

    public LogEvent(UUID id, Instant timestamp, String source, Severity severity, String message, Map<String, String> fields) {
        this.id = Objects.requireNonNull(id, "id");
        this.timestamp = Objects.requireNonNull(timestamp, "timestamp");
        this.source = Objects.requireNonNull(source, "source");
        this.severity = Objects.requireNonNull(severity, "severity");
        this.message = Objects.requireNonNull(message, "message");
        this.fields = Map.copyOf(Objects.requireNonNull(fields, "fields"));
    }

    public static LogEvent create(Instant timestamp, String source, Severity severity, String message, Map<String, String> fields) {
        return new LogEvent(UUID.randomUUID(), timestamp, source, severity, message, fields);
    }

    public UUID id() { return id; }
    public Instant timestamp() { return timestamp; }
    public String source() { return source; }
    public Severity severity() { return severity; }
    public String message() { return message; }
    public Map<String, String> fields() { return fields; }
}
