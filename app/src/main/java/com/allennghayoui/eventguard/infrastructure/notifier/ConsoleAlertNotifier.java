package com.allennghayoui.eventguard.infrastructure.notifier;

import com.allennghayoui.eventguard.domain.Alert;
import com.allennghayoui.eventguard.usecase.port.IAlertNotifier;

public class ConsoleAlertNotifier implements IAlertNotifier {
    
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
