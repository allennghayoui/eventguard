package com.allennghayoui.eventguard.infrastructure.web;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.allennghayoui.eventguard.domain.LogEvent;
import static com.allennghayoui.eventguard.infrastructure.web.InputLimits.MAX_PAGE_SIZE;
import static com.allennghayoui.eventguard.infrastructure.web.InputLimits.MAX_TIME_RANGE_DAYS;
import com.allennghayoui.eventguard.infrastructure.web.dto.IngestLogLineRequest;
import com.allennghayoui.eventguard.infrastructure.web.dto.LogEventResponse;
import com.allennghayoui.eventguard.usecase.IngestLogLine;
import com.allennghayoui.eventguard.usecase.SearchLogEvents;
import com.allennghayoui.eventguard.usecase.port.PaginatedRequest;

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
    public ResponseEntity<List<LogEventResponse>> findBySource(
        @RequestParam String source,
        @RequestParam(defaultValue="0") int page,
        @RequestParam(defaultValue="50") int size
    ) {
        if (size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("size must be <= " + MAX_PAGE_SIZE
            );
        }

        PaginatedRequest paginatedRequest = new PaginatedRequest(page, size);

        List<LogEventResponse> body = searchLogEvents.bySource(source, paginatedRequest).stream()
            .map(LogEventResponse::fromDomain)
            .toList();

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @GetMapping("/by-time")
    public ResponseEntity<List<LogEventResponse>> findInTimeRange(
        @RequestParam Instant from,
        @RequestParam Instant to,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "50") int size 
    ) {
        long days = Duration.between(from, to).toDays();
        if (days > MAX_TIME_RANGE_DAYS) {
            throw new IllegalArgumentException(
                "time range exceeds " + MAX_TIME_RANGE_DAYS + " days"
            );
        }

        if (size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException(
                "size must be <= " + MAX_PAGE_SIZE
            );
        }

        PaginatedRequest paginatedRequest = new PaginatedRequest(page, size);

        List<LogEventResponse> body = searchLogEvents.inTimeRange(from, to, paginatedRequest).stream()
            .map(LogEventResponse::fromDomain)
            .toList();

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<LogEventResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(LogEventResponse.fromDomain(searchLogEvents.byId(id)));
    }
    
}
