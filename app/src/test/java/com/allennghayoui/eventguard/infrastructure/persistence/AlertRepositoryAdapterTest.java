package com.allennghayoui.eventguard.infrastructure.persistence;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.context.annotation.Import;

import com.allennghayoui.eventguard.domain.Alert;
import com.allennghayoui.eventguard.domain.LogEvent;
import com.allennghayoui.eventguard.domain.Severity;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
@Import({AlertRepositoryAdapter.class, LogEventRepositoryAdapter.class})
public class AlertRepositoryAdapterTest {
    
    @Autowired
    private AlertRepositoryAdapter alertRepositoryAdapter;

    @Autowired
    private LogEventRepositoryAdapter logEventRepositoryAdapter;

    @Test
    void savesAlertWithForeignKeyToEvent() {
        LogEvent event = LogEvent.create(
            Instant.parse("2025-01-01T00:00:00Z"),
            "syslog",
            Severity.WARNING,
            "msg",
            Map.of()
        );

        logEventRepositoryAdapter.save(event);

        Alert alert = new Alert(
            UUID.randomUUID(),
            "ssh-bruteforce",
            event.id(),
            Severity.CRITICAL,
            Instant.parse("2025-01-01T12:00:01Z")
        );

        alertRepositoryAdapter.save(alert);

        Optional<Alert> found = alertRepositoryAdapter.findById(alert.id());

        assertThat(found).isPresent();
        assertThat(found.get().ruleName()).isEqualTo("ssh-bruteforce");
        assertThat(found.get().triggeringEventId()).isEqualTo(event.id());
    }

    @Test
    void rejectsAlertReferencingMissingEvent() {
        Alert alert = new Alert(
            UUID.randomUUID(),
            "ssh-bruteforce",
            UUID.randomUUID(),
            Severity.CRITICAL,
            Instant.now()
        );

        assertThatThrownBy(() -> alertRepositoryAdapter.save(alert))
            .isInstanceOf(IllegalStateException.class);
    }
}
