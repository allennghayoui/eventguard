package com.allennghayoui.eventguard.usecase;

import java.util.Objects;

import com.allennghayoui.eventguard.domain.LogEvent;
import com.allennghayoui.eventguard.usecase.port.LogEventRepository;
import com.allennghayoui.eventguard.usecase.port.LogParser;

public class IngestLogLine {
    private final LogParser parser;
    private final LogEventRepository repository;
    private final EvaluateRulesForEvent evaluateRules;

    public IngestLogLine(LogParser parser, LogEventRepository repository, EvaluateRulesForEvent evaluateRules) {
        this.parser = Objects.requireNonNull(parser, "parser");
        this.repository = Objects.requireNonNull(repository, "repository");
        this.evaluateRules = Objects.requireNonNull(evaluateRules, "evaluateRules");
    }

    public LogEvent execute(String rawLine, String source) {
        Objects.requireNonNull(rawLine, "rawLine");
        Objects.requireNonNull(source, "source");

        if (rawLine.isBlank()) {
            throw new IllegalArgumentException("rawLine must not be blank.");
        }

        if (source.isBlank()) {
            throw new IllegalArgumentException("source must not be blank.");
        }

        LogEvent event = parser.parse(rawLine, source);
        repository.save(event);
        evaluateRules.execute(event);
        return event;
    }
}
