package com.allennghayoui.eventguard.usecase;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import com.allennghayoui.eventguard.domain.Alert;
import com.allennghayoui.eventguard.domain.LogEvent;
import com.allennghayoui.eventguard.domain.Severity;
import com.allennghayoui.eventguard.usecase.rule.SshBruteForceRule;

public class EvaluateRulesForEventTest {
    private static final Instant FIXED = Instant.parse("2025-01-01T00:00:00Z");

    @Test
    void firesAlertWhenRuleMatches() {
        InMemoryAlertRepository alertRepository = new InMemoryAlertRepository();
        CapturingAlertNotifier alertNotifier = new CapturingAlertNotifier();
        EvaluateRulesForEvent evaluate = new EvaluateRulesForEvent(
            List.of(new SshBruteForceRule()),
            alertRepository,
            alertNotifier,
            new FixedClock(FIXED)
        );

        LogEvent event = LogEvent.create(
            FIXED,
            "syslog",
            Severity.WARNING,
            "Jan 5 12:34:56 host sshd: Failed password for root from 1.2.3.4",
            Map.of()
        );

        List<Alert> alerts = evaluate.execute(event);

        assertThat(alerts).hasSize(1);
        assertThat(alerts.get(0).ruleName()).isEqualTo("ssh-bruteforce");
        assertThat(alerts.get(0).raisedAt()).isEqualTo(FIXED);
        assertThat(alertRepository.size()).isEqualTo(1);
        assertThat(alertNotifier.notified()).hasSize(1);
    }

    @Test
    void noAlertWhenNoRuleMatches() {
        InMemoryAlertRepository alertRepository = new InMemoryAlertRepository();
        CapturingAlertNotifier alertNotifier = new CapturingAlertNotifier();
        EvaluateRulesForEvent evaluate = new EvaluateRulesForEvent(
            List.of(new SshBruteForceRule()),
            alertRepository,
            alertNotifier,
            new FixedClock(FIXED)
        );

        LogEvent event = LogEvent.create(
            FIXED,
            "syslog",
            Severity.WARNING,
            "some random event message",
            Map.of()
        );

        List<Alert> alerts = evaluate.execute(event);

        assertThat(alerts).isEmpty();
        assertThat(alertRepository.size()).isZero();
        assertThat(alertNotifier.notified()).isEmpty();
    }
}