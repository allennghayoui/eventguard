package com.allennghayoui.eventguard.infrastructure.clock;

import java.time.Instant;

import com.allennghayoui.eventguard.usecase.port.Clock;

public class SystemClock implements Clock {
    @Override
    public Instant now() {
        return Instant.now();
    }
}
