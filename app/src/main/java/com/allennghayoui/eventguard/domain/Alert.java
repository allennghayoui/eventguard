package com.allennghayoui.eventguard.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class Alert {

    private final UUID id;
    private final String ruleName;
    private final UUID triggeringEventId;
    private final Severity severity;
    private final Instant raisedAt;

    public Alert(UUID id, String ruleName, UUID triggeringEventId, Severity severity, Instant raisedAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.ruleName = Objects.requireNonNull(ruleName, "ruleName");
        this.triggeringEventId = Objects.requireNonNull(triggeringEventId, "triggeringEventId");
        this.severity = Objects.requireNonNull(severity, "severity");
        this.raisedAt = Objects.requireNonNull(raisedAt, "raisedAt");
    }

    public static Alert raise(Rule rule, LogEvent event, Instant now) {
        return new Alert(UUID.randomUUID(), rule.name(), event.id(), rule.severity(), now);
    }

    public UUID id() { return id; }
    public String ruleName() { return ruleName; }
    public UUID triggeringEventId() { return triggeringEventId; }
    public Severity severity() { return severity; }
    public Instant raisedAt() { return raisedAt; }
}
