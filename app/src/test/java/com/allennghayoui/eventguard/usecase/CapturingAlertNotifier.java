package com.allennghayoui.eventguard.usecase;

import java.util.ArrayList;
import java.util.List;

import com.allennghayoui.eventguard.domain.Alert;
import com.allennghayoui.eventguard.usecase.port.AlertNotifier;

public class CapturingAlertNotifier implements AlertNotifier {
    private final List<Alert> notified = new ArrayList<>();

    @Override public void notify(Alert alert) { notified.add(alert); }

    public List<Alert> notified() { return List.copyOf(notified); }
}
