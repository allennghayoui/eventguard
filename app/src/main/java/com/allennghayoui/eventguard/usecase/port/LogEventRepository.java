package com.allennghayoui.eventguard.usecase.port;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.allennghayoui.eventguard.domain.LogEvent;

public interface LogEventRepository {
    void save(LogEvent event);
    Optional<LogEvent> findById(UUID id);
    List<LogEvent> findBySource(String source);
    List<LogEvent> findInTimeRange(Instant from, Instant to);
}
