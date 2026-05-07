package com.allennghayoui.eventguard.infrastructure.persistence.entity;

import java.time.Instant;
import java.util.UUID;

import com.allennghayoui.eventguard.domain.Alert;
import com.allennghayoui.eventguard.domain.Severity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="alerts")
public class AlertEntity {
    
    @Id
    private UUID id;

    @Column(name="rule_name", nullable=false)
    private String ruleName;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name = "triggering_event_id")
    private LogEventEntity triggeringEvent;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Severity severity;

    @Column(name="raised_at", nullable=false)
    private Instant raisedAt;

    protected AlertEntity() {}

    private AlertEntity(UUID id, String ruleName, LogEventEntity triggeringEvent, Severity severity, Instant raisedAt) {
        this.id = id;
        this.ruleName = ruleName;
        this.triggeringEvent = triggeringEvent;
        this.severity = severity;
        this.raisedAt = raisedAt;
    }

    public static AlertEntity fromDomain(Alert alert, LogEventEntity triggeringEvent) {
        return new AlertEntity(alert.id(), alert.ruleName(),
            triggeringEvent, alert.severity(), alert.raisedAt()
        );
    }

    public Alert toDomain() {
        return new Alert(id, ruleName, triggeringEvent.getId(), severity, raisedAt);
    }
}
