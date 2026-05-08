package com.allennghayoui.eventguard.infrastructure.web.dto;

import java.time.Instant;
import java.util.UUID;

import com.allennghayoui.eventguard.domain.Alert;
import com.allennghayoui.eventguard.domain.Severity;

public record AlertResponse(
    UUID id,
    String ruleName,
    UUID triggeringEventId,
    Severity severity,
    Instant raisedAt
) {
    public static AlertResponse fromDomain(Alert alert) {
        return new AlertResponse(
            alert.id(),
            alert.ruleName(),
            alert.triggeringEventId(),
            alert.severity(),
            alert.raisedAt()
        );
    }
}
