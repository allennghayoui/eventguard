package com.allennghayoui.eventguard.usecase;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

import com.allennghayoui.eventguard.domain.LogEvent;
import com.allennghayoui.eventguard.domain.Severity;
import com.allennghayoui.eventguard.infrastructure.persistence.InMemoryAlertRepository;
import com.allennghayoui.eventguard.infrastructure.persistence.InMemoryLogEventRepository;
import com.allennghayoui.eventguard.usecase.port.LogParser;
import com.allennghayoui.eventguard.usecase.rule.SshBruteForceRule;

public class IngestLogLineTest {
    @Test
    void parsesAndStoresEvent() {
        InMemoryLogEventRepository repository = new InMemoryLogEventRepository();
        IngestLogLine ingest = new IngestLogLine(new StubLogParser(), repository, noOpEvaluator());

        LogEvent result = ingest.execute("Jan 5 12:34:56 host sshd: hello", "syslog");

        assertThat(repository.size()).isEqualTo(1);
        assertThat(repository.findById(result.id())).isPresent();
        assertThat(result.source()).isEqualTo("syslog");
        assertThat(result.message()).isEqualTo("Jan 5 12:34:56 host sshd: hello");
    }

    @Test
    void rejectsBlankRawLine() {
        IngestLogLine ingest = new IngestLogLine(new StubLogParser(), new InMemoryLogEventRepository(), noOpEvaluator());

        assertThatThrownBy(() -> ingest.execute("  ", "syslog"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("rawLine");
    }

    @Test
    void rejectsBlankSource() {
        IngestLogLine ingest = new IngestLogLine(new StubLogParser(), new InMemoryLogEventRepository(), noOpEvaluator());

        assertThatThrownBy(() -> ingest.execute("some line", " "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("source");
    }

    @Test
    void rejectsNullInputs() {
        IngestLogLine ingest = new IngestLogLine(new StubLogParser(), new InMemoryLogEventRepository(), noOpEvaluator());

        assertThatThrownBy(() -> ingest.execute(null, "syslog"))
            .isInstanceOf(NullPointerException.class);
    
        assertThatThrownBy(() -> ingest.execute("some line", null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void ingestionTriggersRuleEvaluation() {
        InMemoryLogEventRepository eventRepository = new InMemoryLogEventRepository();
        InMemoryAlertRepository alertRepository = new InMemoryAlertRepository();
        CapturingAlertNotifier alertNotifier = new CapturingAlertNotifier();
        EvaluateRulesForEvent evaluate = new EvaluateRulesForEvent(
            List.of(new SshBruteForceRule()),
            alertRepository,
            alertNotifier,
            new FixedClock(Instant.parse("2025-01-01T00:00:00Z"))
        );
        
        LogParser matchingParser = (line, source) -> LogEvent.create(
            Instant.parse("2025-01-01T00:00:00Z"),
            source,
            Severity.WARNING,
            "sshd: Failed password",
            Map.of()
        );

        IngestLogLine ingest = new IngestLogLine(matchingParser, eventRepository, evaluate);
        ingest.execute("any line", "syslog");

        assertThat(alertRepository.size()).isEqualTo(1);
        assertThat(alertNotifier.notified()).hasSize(1);
        
    }

    private EvaluateRulesForEvent noOpEvaluator() {
        return new EvaluateRulesForEvent(
            List.of(),
            new InMemoryAlertRepository(),
            new CapturingAlertNotifier(),
            new FixedClock(Instant.parse("2025-01-01T00:00:00Z"))
        );
    }
}
