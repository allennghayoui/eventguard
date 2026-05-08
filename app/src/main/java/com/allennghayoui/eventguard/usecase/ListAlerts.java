package com.allennghayoui.eventguard.usecase;

import java.util.List;
import java.util.Objects;

import com.allennghayoui.eventguard.domain.Alert;
import com.allennghayoui.eventguard.usecase.port.AlertRepository;

public class ListAlerts {
    private final AlertRepository alertRepository;

    public ListAlerts(AlertRepository alertRepository) {
        this.alertRepository = Objects.requireNonNull(alertRepository, "alertRepository");
    }

    public List<Alert> all() {
        return alertRepository.findAll();
    }

    public List<Alert> byRuleName(String ruleName) {
        Objects.requireNonNull(ruleName, "ruleName");
        return alertRepository.findByRuleName(ruleName);
    }
}
