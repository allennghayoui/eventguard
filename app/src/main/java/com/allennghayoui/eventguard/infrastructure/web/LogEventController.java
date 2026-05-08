package com.allennghayoui.eventguard.infrastructure.web;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.allennghayoui.eventguard.domain.LogEvent;
import com.allennghayoui.eventguard.infrastructure.web.dto.IngestLogLineRequest;
import com.allennghayoui.eventguard.infrastructure.web.dto.LogEventResponse;
import com.allennghayoui.eventguard.usecase.IngestLogLine;
import com.allennghayoui.eventguard.usecase.SearchLogEvents;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/events")
public class LogEventController {
    private final IngestLogLine ingestLogLine;
    private final SearchLogEvents searchLogEvents;

    public LogEventController(IngestLogLine ingestLogLine, SearchLogEvents searchLogEvents) {
        this.ingestLogLine = ingestLogLine;
        this.searchLogEvents = searchLogEvents;
    }

    @PostMapping
    public ResponseEntity<LogEventResponse> ingest(@Valid @RequestBody IngestLogLineRequest request) {
        LogEvent event = ingestLogLine.execute(request.rawLine(), request.source());
        return ResponseEntity.status(HttpStatus.CREATED).body(LogEventResponse.fromDomain(event));
    }

    @GetMapping("/by-source")
    public ResponseEntity<List<LogEventResponse>> findBySource(@RequestParam String source) {
        List<LogEventResponse> body = searchLogEvents.bySource(source).stream()
            .map(LogEventResponse::fromDomain)
            .toList();

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @GetMapping("/by-time")
    public ResponseEntity<List<LogEventResponse>> findInTimeRange(@RequestParam Instant from, @RequestParam Instant to) {
        List<LogEventResponse> body = searchLogEvents.inTimeRange(from, to).stream()
            .map(LogEventResponse::fromDomain)
            .toList();

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }
    
}
