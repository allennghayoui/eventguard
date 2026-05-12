package com.allennghayoui.eventguard.infrastructure.persistence;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.allennghayoui.eventguard.domain.LogEvent;
import com.allennghayoui.eventguard.usecase.port.LogEventRepository;
import com.allennghayoui.eventguard.usecase.port.PaginatedRequest;

public class InMemoryLogEventRepository implements LogEventRepository {
    private final Map<UUID, LogEvent> store = new HashMap<>();

    @Override
    public void save(LogEvent event) {
        store.put(event.id(), event);
    }

    @Override
    public Optional<LogEvent> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<LogEvent> findBySource(String source, PaginatedRequest page) {
        return store.values().stream()
            .filter(event -> event.source().equals(source))
            .skip((long) page.page() * page.size())
            .limit(page.size())
            .toList();
    }

    @Override
    public List<LogEvent> findInTimeRange(Instant from, Instant to, PaginatedRequest page) {
        return store.values().stream()
            .filter(event -> !event.timestamp().isBefore(from) && !event.timestamp().isAfter(to))
            .skip((long) page.page() * page.size())
            .limit(page.size())
            .toList();
    }

    public int size() { return store.size(); }
}
