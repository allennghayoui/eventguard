package com.allennghayoui.eventguard.usecase;

import java.util.List;
import java.util.Objects;

import com.allennghayoui.eventguard.domain.Alert;
import com.allennghayoui.eventguard.domain.IRule;
import com.allennghayoui.eventguard.domain.LogEvent;
import com.allennghayoui.eventguard.usecase.port.IAlertNotifier;
import com.allennghayoui.eventguard.usecase.port.IAlertRepository;
import com.allennghayoui.eventguard.usecase.port.IClock;

public class EvaluateRulesForEvent {
    private final List<IRule> rules;
    private final IAlertRepository alertRepository;
    private final IAlertNotifier alertNotifier;
    private final IClock clock;

    public EvaluateRulesForEvent(List<IRule> rules, IAlertRepository alertRepository, IAlertNotifier alertNotifier, IClock clock) {
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
