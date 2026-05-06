package com.allennghayoui.eventguard.domain;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

public class LogEventTest {
    
    @Test
    void createGeneratesId() {
        LogEvent event = LogEvent.create(
            Instant.parse("2025-01-01T00:00:00Z"), 
            "syslog", Severity.INFO, 
            "test", Map.of()
        );

        assertThat(event.id()).isNotNull();
        assertThat(event.source()).isEqualTo("syslog");
    }
    
    @Test
    void rejectNullFields() {
        assertThatThrownBy(() -> new LogEvent(
            null,
            Instant.now(),
            "syslog",
            Severity.INFO,
            "test",
            Map.of())
        ).isInstanceOf(NullPointerException.class);
    }

    @Test
    void fieldsMapIsImmutable() {
        Map<String, String> mutable = new HashMap<>();
        mutable.put("user", "root");

        LogEvent event = LogEvent.create(
            Instant.now(),
            "syslog",
            Severity.INFO,
            "msg",
            mutable
        );

        mutable.put("user", "attacker");

        assertThat(event.fields().get("user")).isEqualTo("root");

        assertThatThrownBy(() -> event.fields().put("x", "y"))
            .isInstanceOf(UnsupportedOperationException.class);
    }
}
