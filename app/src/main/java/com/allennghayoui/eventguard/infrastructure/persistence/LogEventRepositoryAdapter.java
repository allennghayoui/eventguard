package com.allennghayoui.eventguard.infrastructure.persistence;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.allennghayoui.eventguard.domain.LogEvent;
import com.allennghayoui.eventguard.infrastructure.persistence.entity.LogEventEntity;
import com.allennghayoui.eventguard.infrastructure.persistence.jpa.LogEventJpaRepository;
import com.allennghayoui.eventguard.usecase.port.LogEventRepository;

@Repository
@Profile("!dev")
public class LogEventRepositoryAdapter implements LogEventRepository {
    private final LogEventJpaRepository jpa;

    public LogEventRepositoryAdapter(LogEventJpaRepository jpa) {
        this.jpa = Objects.requireNonNull(jpa);
    }

    @Override
    public void save(LogEvent event) {
        jpa.save(LogEventEntity.fromDomain(event));
    }

    @Override
    public Optional<LogEvent> findById(UUID id) {
        return jpa.findById(id).map(LogEventEntity::toDomain);
    }

    @Override
    public List<LogEvent> findBySource(String source) {
        return jpa.findBySource(source).stream()
            .map(LogEventEntity::toDomain)
            .toList();
    }

    @Override
    public List<LogEvent> findInTimeRange(Instant from, Instant to) {
        return jpa.findByTimestampBetween(from, to).stream()
            .map(LogEventEntity::toDomain)
            .toList();
    }
}
