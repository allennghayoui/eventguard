package com.allennghayoui.eventguard.infrastructure.clock;

import java.time.Instant;

import org.springframework.stereotype.Component;

import com.allennghayoui.eventguard.usecase.port.Clock;

@Component
public class SystemClock implements Clock {
    @Override
    public Instant now() {
        return Instant.now();
    }
}
