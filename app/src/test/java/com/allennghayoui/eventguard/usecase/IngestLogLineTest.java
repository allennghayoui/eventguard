package com.allennghayoui.eventguard.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

import com.allennghayoui.eventguard.domain.LogEvent;

public class IngestLogLineTest {
    @Test
    void parsesAndStoresEvent() {
        InMemoryLogEventRepository repository = new InMemoryLogEventRepository();
        IngestLogLine ingest = new IngestLogLine(new StubLogParser(), repository);

        LogEvent result = ingest.execute("Jan 5 12:34:56 host sshd: hello", "syslog");

        assertThat(repository.size()).isEqualTo(1);
        assertThat(repository.findById(result.id())).isPresent();
        assertThat(result.source()).isEqualTo("syslog");
        assertThat(result.message()).isEqualTo("Jan 5 12:34:56 host sshd: hello");
    }

    @Test
    void rejectsBlankRawLine() {
        IngestLogLine ingest = new IngestLogLine(new StubLogParser(), new InMemoryLogEventRepository());

        assertThatThrownBy(() -> ingest.execute("  ", "syslog"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("rawLine");
    }

    @Test
    void rejectsBlankSource() {
        IngestLogLine ingest = new IngestLogLine(new StubLogParser(), new InMemoryLogEventRepository());

        assertThatThrownBy(() -> ingest.execute("some line", " "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("source");
    }

    @Test
    void rejectsNullInputs() {
        IngestLogLine ingest = new IngestLogLine(new StubLogParser(), new InMemoryLogEventRepository());

        assertThatThrownBy(() -> ingest.execute(null, "syslog"))
            .isInstanceOf(NullPointerException.class);
    
        assertThatThrownBy(() -> ingest.execute("some line", null))
            .isInstanceOf(NullPointerException.class);
    }
}
