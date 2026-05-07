package com.allennghayoui.eventguard.infrastructure.persistence.jpa;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.allennghayoui.eventguard.infrastructure.persistence.entity.LogEventEntity;

public interface LogEventJpaRepository extends JpaRepository<LogEventEntity, UUID> {
    List<LogEventEntity> findBySource(String source); 
    List<LogEventEntity> findByTimestampBetween(Instant from, Instant to);
}
