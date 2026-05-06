package com.allennghayoui.eventguard.usecase;

import java.util.Objects;

import com.allennghayoui.eventguard.domain.LogEvent;
import com.allennghayoui.eventguard.usecase.port.ILogEventRepository;
import com.allennghayoui.eventguard.usecase.port.ILogParser;

public class IngestLogLine {
    private final ILogParser parser;
    private final ILogEventRepository repository;

    public IngestLogLine(ILogParser parser, ILogEventRepository repository) {
        this.parser = Objects.requireNonNull(parser, "parser");
        this.repository = Objects.requireNonNull(repository, "repository");
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
        return event;
    }
}
