package com.allennghayoui.eventguard.infrastructure.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.allennghayoui.eventguard.domain.Alert;
import com.allennghayoui.eventguard.usecase.port.AlertRepository;

public class InMemoryAlertRepository implements AlertRepository{
    private final Map<UUID, Alert> store = new HashMap<>();

    @Override public void save(Alert alert) { store.put(alert.id(), alert); }
    @Override public Optional<Alert> findById(UUID id) { return Optional.ofNullable(store.get(id)); }
    @Override public List<Alert> findAll() { return new ArrayList<>(store.values()); }
    @Override public List<Alert> findByRuleName(String ruleName) { 
        return store.values().stream().filter(alert -> alert.ruleName().equals(ruleName)).toList();
    }

    public int size() { return store.size(); }
}
