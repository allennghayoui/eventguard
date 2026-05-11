package com.allennghayoui.eventguard.infrastructure.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.allennghayoui.eventguard.domain.Rule;
import com.allennghayoui.eventguard.infrastructure.clock.SystemClock;
import com.allennghayoui.eventguard.infrastructure.notifier.ConsoleAlertNotifier;
import com.allennghayoui.eventguard.infrastructure.parser.Rfc3164SyslogParser;
import com.allennghayoui.eventguard.infrastructure.persistence.InMemoryAlertRepository;
import com.allennghayoui.eventguard.infrastructure.persistence.InMemoryLogEventRepository;
import com.allennghayoui.eventguard.usecase.EvaluateRulesForEvent;
import com.allennghayoui.eventguard.usecase.IngestLogLine;
import com.allennghayoui.eventguard.usecase.ListAlerts;
import com.allennghayoui.eventguard.usecase.SearchLogEvents;
import com.allennghayoui.eventguard.usecase.port.AlertNotifier;
import com.allennghayoui.eventguard.usecase.port.AlertRepository;
import com.allennghayoui.eventguard.usecase.port.Clock;
import com.allennghayoui.eventguard.usecase.port.LogEventRepository;
import com.allennghayoui.eventguard.usecase.port.LogParser;
import com.allennghayoui.eventguard.usecase.rule.SshBruteForceRule;

@Configuration
public class Wiring {
    
    // Ports

    @Bean
    public LogParser logParser() {
        return new Rfc3164SyslogParser();
    }
    
    @Bean
    public AlertNotifier alertNotifier() {
        return new ConsoleAlertNotifier();
    }
    
    @Bean
    public Clock clock() {
        return new SystemClock();
    }
    
    // Rules

    @Bean
    public List<Rule> rules() {
        return List.of(new SshBruteForceRule());
    }

    // Use cases

    @Bean
    public EvaluateRulesForEvent evaluateRulesForEvent(
        List<Rule> rules,
        AlertRepository alertRepository,
        AlertNotifier alertNotifier,
        Clock clock
    ) {
        return new EvaluateRulesForEvent(rules, alertRepository, alertNotifier, clock);
    }

    @Bean
    public IngestLogLine ingestLogLine(
        LogParser logParser,
        LogEventRepository logEventRepository,
        EvaluateRulesForEvent evaluateRulesForEvent
    ) {
        return new IngestLogLine(logParser, logEventRepository, evaluateRulesForEvent);
    }

    @Bean
    public SearchLogEvents searchLogEvents(LogEventRepository logEventRepository) {
        return new SearchLogEvents(logEventRepository);
    }

    @Bean
    public ListAlerts listAlerts(AlertRepository alertRepository) {
        return new ListAlerts(alertRepository);
    }

    // In-Memory persistence
    @Bean
    @Profile("dev")
    public LogEventRepository inMemoryLogEventRepository() {
        return new InMemoryLogEventRepository();
    }

    @Bean
    @Profile("dev")
    public AlertRepository inMemoryAlertRepository() {
        return new InMemoryAlertRepository();
    }
}
