package com.allennghayoui.eventguard.usecase;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

import com.allennghayoui.eventguard.domain.LogEvent;
import com.allennghayoui.eventguard.domain.Severity;

public class SearchLogEventsTest {
    @Test
    void findsBySource() {
        InMemoryLogEventRepository eventRepository = new InMemoryLogEventRepository();
        eventRepository.save(LogEvent.create(Instant.now(), "syslog", Severity.INFO, "msg a", Map.of()));
        eventRepository.save(LogEvent.create(Instant.now(), "nginx", Severity.INFO, "msg b", Map.of()));

        SearchLogEvents search = new SearchLogEvents(eventRepository);

        assertThat(search.bySource("syslog")).hasSize(1);
        assertThat(search.bySource("nginx")).hasSize(1);
        assertThat(search.bySource("missing")).isEmpty();
    }

    @Test
    void rejectsReversedTimeRange() {
        SearchLogEvents search = new SearchLogEvents(new InMemoryLogEventRepository());

        assertThatThrownBy(() ->
            search.inTimeRange(Instant.parse("2025-01-02T00:00:00Z"), Instant.parse("2025-01-01T00:00:00Z"))
        ).isInstanceOf(IllegalArgumentException.class);
    }
}
