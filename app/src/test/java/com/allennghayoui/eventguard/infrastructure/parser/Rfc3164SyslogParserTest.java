package com.allennghayoui.eventguard.infrastructure.parser;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import com.allennghayoui.eventguard.domain.LogEvent;
import com.allennghayoui.eventguard.domain.Severity;

public class Rfc3164SyslogParserTest {
    private final Rfc3164SyslogParser parser = new Rfc3164SyslogParser();

    @Test
    void parsesRfc3164WithPriority() {
        LogEvent event = parser.parse(
            "<13>Jan  5 12:34:56 web01 sshd[1234]: Failed password for root",
            "syslog"
        );

        assertThat(event.fields()).containsEntry("hostname", "web01");
        assertThat(event.fields()).containsEntry("program", "sshd");
        assertThat(event.fields()).containsEntry("pid", "1234");
        assertThat(event.fields()).containsEntry("syslog.facility", "1");
        assertThat(event.fields()).containsEntry("syslog.severity", "5");
        assertThat(event.message()).isEqualTo("Failed password for root");
    }

    @Test
    void parsesRfc3164WithoutPriority() {
        LogEvent event = parser.parse(
            "Jan  5 12:34:56 web01 sshd[1234]: Failed password for root",
            "syslog"
        );

        assertThat(event.fields()).containsEntry("hostname", "web01");
        assertThat(event.fields()).containsEntry("program", "sshd");
        assertThat(event.fields()).containsEntry("pid", "1234");
        assertThat(event.fields()).doesNotContainKey("syslog.facility");
        assertThat(event.message()).isEqualTo("Failed password for root");
    }

    @Test
    void parsesWithoutPid() {
        LogEvent event = parser.parse(
            "Jan  5 12:34:56 web01 systemd: Started Session 5",
            "syslog"
        );

        assertThat(event.fields()).containsEntry("program", "systemd");
        assertThat(event.fields()).doesNotContainKey("pid");
        assertThat(event.message()).isEqualTo("Started Session 5");
    }
    
    @Test
    void parsesTimestampToCurrentYear() {
        LogEvent event = parser.parse(
            "Jan  5 12:34:56 web01 systemd: msg",
            "syslog"
        );        

        int currentYear = LocalDateTime.now(ZoneOffset.UTC).getYear();
        LocalDateTime expected = LocalDateTime.of(currentYear, 1, 5, 12, 34, 56);

        assertThat(event.timestamp())
            .isEqualTo(expected.toInstant(ZoneOffset.UTC));
    }

    @Test
    void severityFromPriorityOverridesKeywordHeuristic() {
        LogEvent event = parser.parse(
            "<8>Jan  5 12:34:56 web01 systemd: just an info message",
            "syslog"
        );    

        assertThat(event.severity()).isEqualTo(Severity.CRITICAL);
    }

    @Test
    void severityFromKeywordHeuristicWhenNoPriority() {
        LogEvent event = parser.parse(
            "Jan  5 12:34:56 web01 sshd: Failed password",
            "syslog"
        );

        assertThat(event.severity()).isEqualTo(Severity.WARNING);
    }

    @Test
    void severityDefaultsToInfoForBenignMessage() {
        LogEvent event = parser.parse(
            "Jan  5 12:34:56 web01 systemd: Started Session 5",
            "syslog"
        );

        assertThat(event.severity()).isEqualTo(Severity.INFO);
    }

    @Test
    void invalidPriorityCodeIsIgnored() {
        LogEvent event = parser.parse(
            "<999>Jan  5 12:34:56 web01 sshd: msg",
            "syslog"
        );

        assertThat(event.fields()).doesNotContainKey("syslog.facility");
        assertThat(event.fields()).containsEntry("program", "sshd");
    }

    @Test
    void nonNumericPriorityIsTolerated() {
        LogEvent event = parser.parse(
            "<abc>Jan  5 12:34:56 web01 sshd: msg",
            "syslog"
        );

        assertThat(event).isNotNull();
    }

    @Test
    void unparseableInputFallsBackGracefully() {
        LogEvent event = parser.parse(
            "this is not syslog at all",
            "syslog"
        );
        
        assertThat(event.fields()).containsEntry("parseError", "rfc3164-no-match");
        assertThat(event.message()).isEqualTo("this is not syslog at all");
        assertThat(event.severity()).isEqualTo(Severity.INFO);
    }

    @Test
    void emptyInputDoesNotCrash() {
        LogEvent event = parser.parse(
            "",
            "syslog"
        );
    
        assertThat(event).isNotNull();
        assertThat(event.fields()).containsEntry("parseError", "rfc3164-no-match");
    }

    @Test
    void nullInputDoesNotCrash() {
        LogEvent event = parser.parse(
            null,
            "syslog"
        );
    
        assertThat(event).isNotNull();
    }

    @Test
    void preservesRawLine() {
        String input = "<13>Jan  5 12:34:56 web01 sshd[1234]: Failed password";
        LogEvent event = parser.parse(
            input,
            "syslog"
        );
    
        assertThat(event.fields()).containsEntry("raw", input);
    }

    @Test
    void rawLinePreservedEvenOnParseFailure() {
        String input = "this is not syslog at all";
        LogEvent event = parser.parse(
            input,
            "syslog"
        );
    
        assertThat(event.fields()).containsEntry("raw", input);
        assertThat(event.fields()).containsEntry("parseError", "rfc3164-no-match");
    }
}
