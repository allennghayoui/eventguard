package com.allennghayoui.eventguard;

import java.util.List;

import com.allennghayoui.eventguard.domain.LogEvent;
import com.allennghayoui.eventguard.infrastructure.clock.SystemClock;
import com.allennghayoui.eventguard.infrastructure.notifier.ConsoleAlertNotifier;
import com.allennghayoui.eventguard.infrastructure.parser.SimpleSyslogParser;
import com.allennghayoui.eventguard.infrastructure.persistence.InMemoryAlertRepository;
import com.allennghayoui.eventguard.infrastructure.persistence.InMemoryLogEventRepository;
import com.allennghayoui.eventguard.usecase.EvaluateRulesForEvent;
import com.allennghayoui.eventguard.usecase.IngestLogLine;
import com.allennghayoui.eventguard.usecase.SearchLogEvents;
import com.allennghayoui.eventguard.usecase.port.IAlertNotifier;
import com.allennghayoui.eventguard.usecase.port.IAlertRepository;
import com.allennghayoui.eventguard.usecase.port.IClock;
import com.allennghayoui.eventguard.usecase.port.ILogEventRepository;
import com.allennghayoui.eventguard.usecase.port.ILogParser;
import com.allennghayoui.eventguard.usecase.rule.SshBruteForceRule;

public class Main {
    public static void main(String[] args) {
        // Adapters
        ILogParser parser = new SimpleSyslogParser();
        ILogEventRepository eventRepository = new InMemoryLogEventRepository();
        IAlertRepository alertRepository = new InMemoryAlertRepository();
        IAlertNotifier alertNotifier = new ConsoleAlertNotifier();
        IClock clock = new SystemClock();

        // Use cases
        EvaluateRulesForEvent evaluateRules = new EvaluateRulesForEvent(
            List.of(new SshBruteForceRule()),
            alertRepository,
            alertNotifier,
            clock
        );

        IngestLogLine ingest = new IngestLogLine(
            parser,
            eventRepository,
            evaluateRules
        );

        SearchLogEvents search = new SearchLogEvents(eventRepository);

        // Demo
        System.out.println("=== EventGuard demo ===\n");

        String[] sampleLines = {
            "Jan  5 12:34:56 web01 sshd[1234]: Failed password for root from 192.168.1.50 port 22",
            "Jan  5 12:35:00 web01 sshd[1235]: Accepted publickey for alice from 10.0.0.5",
            "Jan  5 12:35:10 web01 sshd[1236]: Failed password for admin from 192.168.1.50 port 22",
            "Jan  5 12:36:00 web01 systemd[1]: Started Session 5 of user alice",
        };

        for (String line : sampleLines) {
            System.out.println("Ingesting: " + line);
            LogEvent event = ingest.execute(line, "syslog");
            System.out.printf(" -> stored event %s (severity=%s)%n%n", event.id(), event.severity());
        }

        System.out.println("=== Store syslog events ===");
        for (LogEvent event : search.bySource("syslog")) {
            System.out.printf(" %s [%s] %s%n",
                event.timestamp(), event.severity(), event.message()
            );
        }
    }
}
