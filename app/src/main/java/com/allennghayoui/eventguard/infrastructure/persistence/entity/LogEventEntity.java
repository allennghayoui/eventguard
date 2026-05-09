package com.allennghayoui.eventguard.infrastructure.persistence.entity;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.allennghayoui.eventguard.domain.LogEvent;
import com.allennghayoui.eventguard.domain.Severity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;

@Entity
@Table(name="log_events")
public class LogEventEntity {
    
    @Id
    private UUID id;

    @Column(nullable=false)
    private Instant timestamp;

    @Column(nullable=false)
    private String source;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Severity severity;

    @Column(nullable=false, length=4000)
    private String message;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="log_event_fields", joinColumns=@JoinColumn(name="event_id"))
    @MapKeyColumn(name="field_key")
    @Column(name="field_value", columnDefinition="TEXT")
    private Map<String, String> fields = new HashMap<>();

    protected LogEventEntity() {}

    private LogEventEntity(UUID id, Instant timestamp, String source, Severity severity, String message, Map<String, String> fields) {
        this.id = id;
        this.timestamp = timestamp;
        this.source = source;
        this.severity = severity;
        this.message = message;
        this.fields = new HashMap<>(fields);
    }

    public static LogEventEntity fromDomain(LogEvent event) {
        return new LogEventEntity(
            event.id(), event.timestamp(), event.source(),
            event.severity(), event.message(), event.fields()
        );
    }

    public LogEvent toDomain() {
        return new LogEvent(id, timestamp, source, severity, message, fields);
    }

    public UUID getId() { return id; }
}
