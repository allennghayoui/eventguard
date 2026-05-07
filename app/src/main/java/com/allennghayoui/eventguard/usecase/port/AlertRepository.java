package com.allennghayoui.eventguard.usecase.port;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.allennghayoui.eventguard.domain.Alert;

public interface AlertRepository {
    void save(Alert alert);
    Optional<Alert> findById(UUID id);
    List<Alert> findAll();
    List<Alert> findByRuleName(String ruleName);
}
