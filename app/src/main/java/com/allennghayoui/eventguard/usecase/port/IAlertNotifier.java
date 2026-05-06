package com.allennghayoui.eventguard.usecase.port;

import com.allennghayoui.eventguard.domain.Alert;

public interface IAlertNotifier {
    void notify(Alert alert);
}
