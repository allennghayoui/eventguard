package com.allennghayoui.eventguard.infrastructure.persistence;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.context.annotation.Import;

import com.allennghayoui.eventguard.domain.LogEvent;
import com.allennghayoui.eventguard.domain.Severity;
import com.allennghayoui.eventguard.infrastructure.persistence.jpa.LogEventJpaRepository;
import com.allennghayoui.eventguard.usecase.port.PaginatedRequest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
@Import(LogEventRepositoryAdapter.class)
public class LogEventRepositoryAdapterTest {
    
    @Autowired
    private LogEventRepositoryAdapter logEventRepositoryAdapter;

    @Autowired
    private LogEventJpaRepository jpa;

    @Test
    void savesAndFindsByIdAndSource() {
        LogEvent event = LogEvent.create(
            Instant.parse("2025-01-01T00:00:00Z"),
            "syslog",
            Severity.WARNING,
            "Failed login",
            Map.of("user", "root", "ip", "1.2.3.4")
        );

        logEventRepositoryAdapter.save(event);

        assertThat(jpa.count()).isEqualTo(1);

        Optional<LogEvent> found = logEventRepositoryAdapter.findById(event.id());

        assertThat(found).isPresent();
        assertThat(found.get().message()).isEqualTo("Failed login");
        assertThat(found.get().fields()).containsEntry("user", "root");

        PaginatedRequest paginatedRequest = new PaginatedRequest(0,50);

        List<LogEvent> bySource = logEventRepositoryAdapter.findBySource("syslog", paginatedRequest);
        assertThat(bySource).hasSize(1);
    }

    @Test
    void findByTimeRangeReturnsOnlyMatching() {
        Instant t1 = Instant.parse("2025-01-01T00:00:00Z");
        Instant t2 = Instant.parse("2025-01-15T00:00:00Z");
        Instant t3 = Instant.parse("2025-02-01T00:00:00Z");

        logEventRepositoryAdapter.save(LogEvent.create(t1, "syslog", Severity.INFO, "first", Map.of()));
        logEventRepositoryAdapter.save(LogEvent.create(t2, "syslog", Severity.INFO, "second", Map.of()));
        logEventRepositoryAdapter.save(LogEvent.create(t3, "syslog", Severity.INFO, "third", Map.of()));

        PaginatedRequest paginatedRequest = new PaginatedRequest(0,50);

        List<LogEvent> found = logEventRepositoryAdapter.findInTimeRange(
            Instant.parse("2025-01-10T00:00:00Z"),
            Instant.parse("2025-01-20T00:00:00Z"),
            paginatedRequest
        );

        assertThat(found).hasSize(1);
        assertThat(found.get(0).message()).isEqualTo("second");
    }
}
