package com.allennghayoui.eventguard.infrastructure.clock;

import java.time.Instant;

import com.allennghayoui.eventguard.usecase.port.IClock;

public class SystemClock implements IClock {
    @Override
    public Instant now() {
        return Instant.now();
    }
}
