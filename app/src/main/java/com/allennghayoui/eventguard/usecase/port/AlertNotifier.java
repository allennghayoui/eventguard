package com.allennghayoui.eventguard.usecase.port;

import com.allennghayoui.eventguard.domain.Alert;

public interface AlertNotifier {
    void notify(Alert alert);
}
