package com.allennghayoui.eventguard.usecase;

import java.util.List;
import java.util.Objects;

import com.allennghayoui.eventguard.domain.Alert;
import com.allennghayoui.eventguard.domain.LogEvent;
import com.allennghayoui.eventguard.domain.Rule;
import com.allennghayoui.eventguard.usecase.port.AlertNotifier;
import com.allennghayoui.eventguard.usecase.port.AlertRepository;
import com.allennghayoui.eventguard.usecase.port.Clock;

public class EvaluateRulesForEvent {
    private final List<Rule> rules;
    private final AlertRepository alertRepository;
    private final AlertNotifier alertNotifier;
    private final Clock clock;

    public EvaluateRulesForEvent(List<Rule> rules, AlertRepository alertRepository, AlertNotifier alertNotifier, Clock clock) {
        this.rules = List.copyOf(Objects.requireNonNull(rules, "rules"));
        this.alertRepository = Objects.requireNonNull(alertRepository, "alertRepository");
        this.alertNotifier = Objects.requireNonNull(alertNotifier, "alertNotifier");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public List<Alert> execute(LogEvent event) {
        Objects.requireNonNull(event, "event");

        return rules.stream()
            .filter(rule -> rule.matches(event))
            .map(rule -> {
                Alert alert = Alert.raise(rule, event, clock.now());
                alertRepository.save(alert);
                alertNotifier.notify(alert);
                return alert;
            })
            .toList();
    }
}
