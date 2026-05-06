package com.allennghayoui.eventguard.domain;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

public class AlertTest {
    @Test
    void raiseCopiesNameAndSeverityFromRule() {
        Rule rule = new Rule() {
            @Override public String name() { return "ssh-bruteforce"; }
            @Override public Severity severity() { return Severity.CRITICAL; }
            @Override public boolean matches(LogEvent event) { return true; }
        };

        LogEvent event = LogEvent.create(
            Instant.parse("2025-01-01T00:00:00Z"),
            "syslog",
            Severity.WARNING,
            "Failed password",
            Map.of()
        );
    
        Instant raisedAt = Instant.parse("2025-01-01T00:00:00Z");
        Alert alert = Alert.raise(rule, event, raisedAt);

        assertThat(alert.ruleName()).isEqualTo(rule.name());
        assertThat(alert.severity()).isEqualTo(rule.severity());
        assertThat(alert.triggeringEventId()).isEqualTo(event.id());
        assertThat(alert.raisedAt()).isEqualTo(raisedAt);
        assertThat(alert.id()).isNotNull();
    }
}

