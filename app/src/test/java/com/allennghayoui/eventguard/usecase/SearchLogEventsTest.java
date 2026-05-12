package com.allennghayoui.eventguard.usecase;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

import com.allennghayoui.eventguard.domain.LogEvent;
import com.allennghayoui.eventguard.domain.Severity;
import com.allennghayoui.eventguard.infrastructure.persistence.InMemoryLogEventRepository;
import com.allennghayoui.eventguard.usecase.port.PaginatedRequest;

public class SearchLogEventsTest {
    @Test
    void findsBySource() {
        InMemoryLogEventRepository eventRepository = new InMemoryLogEventRepository();
        eventRepository.save(LogEvent.create(Instant.now(), "syslog", Severity.INFO, "msg a", Map.of()));
        eventRepository.save(LogEvent.create(Instant.now(), "nginx", Severity.INFO, "msg b", Map.of()));

        SearchLogEvents search = new SearchLogEvents(eventRepository);

        PaginatedRequest paginatedRequest = new PaginatedRequest(0, 50);

        assertThat(search.bySource("syslog", paginatedRequest)).hasSize(1);
        assertThat(search.bySource("nginx", paginatedRequest)).hasSize(1);
        assertThat(search.bySource("missing", paginatedRequest)).isEmpty();
    }

    @Test
    void rejectsReversedTimeRange() {
        SearchLogEvents search = new SearchLogEvents(new InMemoryLogEventRepository());

        PaginatedRequest paginatedRequest = new PaginatedRequest(0, 50);

        assertThatThrownBy(() ->
            search.inTimeRange(Instant.parse("2025-01-02T00:00:00Z"), Instant.parse("2025-01-01T00:00:00Z"), paginatedRequest)
        ).isInstanceOf(IllegalArgumentException.class);
    }
}
