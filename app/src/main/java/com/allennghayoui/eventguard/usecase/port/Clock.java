package com.allennghayoui.eventguard.usecase.port;

import java.time.Instant;

public interface Clock {
    Instant now();
}
