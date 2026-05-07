package com.allennghayoui.eventguard.infrastructure.persistence;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.allennghayoui.eventguard.domain.Alert;
import com.allennghayoui.eventguard.infrastructure.persistence.entity.AlertEntity;
import com.allennghayoui.eventguard.infrastructure.persistence.entity.LogEventEntity;
import com.allennghayoui.eventguard.infrastructure.persistence.jpa.AlertJpaRepository;
import com.allennghayoui.eventguard.infrastructure.persistence.jpa.LogEventJpaRepository;
import com.allennghayoui.eventguard.usecase.port.AlertRepository;

@Repository
@Profile("!dev")
public class AlertRepositoryAdapter implements AlertRepository {
    
    private final AlertJpaRepository alertJpa;
    private final LogEventJpaRepository eventJpa;

    public AlertRepositoryAdapter(AlertJpaRepository alertJpa, LogEventJpaRepository eventJpa) {
        this.alertJpa = Objects.requireNonNull(alertJpa);
        this.eventJpa = Objects.requireNonNull(eventJpa);
    }

    @Override
    public void save(Alert alert) {
        LogEventEntity event = eventJpa.findById(alert.triggeringEventId())
            .orElseThrow(() -> new IllegalStateException(
                "Cannot save alert: triggering event " + alert.triggeringEventId() + " not found"
            ));
        alertJpa.save(AlertEntity.fromDomain(alert, event));
    }

    @Override
    public Optional<Alert> findById(UUID id) {
        return alertJpa.findById(id).map(AlertEntity::toDomain);
    }

    @Override
    public List<Alert> findAll() {
        return alertJpa.findAll().stream().map(AlertEntity::toDomain).toList();
    }

    @Override
    public List<Alert> findByRuleName(String ruleName) {
        return alertJpa.findByRuleName(ruleName).stream().map(AlertEntity::toDomain).toList();
    }
}
