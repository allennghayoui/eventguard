package com.allennghayoui.eventguard.usecase;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import com.allennghayoui.eventguard.domain.LogEvent;
import com.allennghayoui.eventguard.usecase.port.ILogEventRepository;

public class SearchLogEvents {
    private final ILogEventRepository repository;

    public SearchLogEvents(ILogEventRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<LogEvent> bySource(String source) {
        Objects.requireNonNull(source, "source");

        return repository.findBySource(source);
    }

    public List<LogEvent> inTimeRange(Instant from, Instant to) {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");

        if (from.isAfter(to)) {
            throw new IllegalArgumentException("from must not be after to");
        }

        return repository.findInTimeRange(from, to);
    }
}
