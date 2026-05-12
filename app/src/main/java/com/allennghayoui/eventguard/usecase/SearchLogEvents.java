package com.allennghayoui.eventguard.usecase;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.allennghayoui.eventguard.domain.LogEvent;
import com.allennghayoui.eventguard.domain.NotFoundException;
import com.allennghayoui.eventguard.usecase.port.LogEventRepository;
import com.allennghayoui.eventguard.usecase.port.PaginatedRequest;

public class SearchLogEvents {
    private final LogEventRepository logEventRepository;

    public SearchLogEvents(LogEventRepository logEventRepository) {
        this.logEventRepository = Objects.requireNonNull(logEventRepository, "logEventRepository");
    }

    public LogEvent byId(UUID id) {
        Objects.requireNonNull(id, "id");
        return logEventRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("No log event with id " + id));
    }

    public List<LogEvent> bySource(String source, PaginatedRequest page) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(page, "page");

        return logEventRepository.findBySource(source, page);
    }

    public List<LogEvent> inTimeRange(Instant from, Instant to, PaginatedRequest page) {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");
        Objects.requireNonNull(page, "page");

        if (from.isAfter(to)) {
            throw new IllegalArgumentException("from must not be after to");
        }

        return logEventRepository.findInTimeRange(from, to, page);
    }
}
