package com.allennghayoui.eventguard.infrastructure.notifier;

import org.springframework.stereotype.Component;

import com.allennghayoui.eventguard.domain.Alert;
import com.allennghayoui.eventguard.usecase.port.AlertNotifier;

@Component
public class ConsoleAlertNotifier implements AlertNotifier {
    
    @Override
    public void notify(Alert alert) {
        System.out.printf(
            "[ALERT] severity=%s rule=%s eventId=%s raisedAt=%s%n",
            alert.severity(),
            alert.ruleName(),
            alert.triggeringEventId(),
            alert.raisedAt()
        );
    }
}
