package com.allennghayoui.eventguard.usecase;

import java.time.Instant;

import com.allennghayoui.eventguard.usecase.port.Clock;

public class FixedClock implements Clock {
    private final Instant fixed;
    
    public FixedClock(Instant fixed) { this.fixed = fixed; }

    @Override public Instant now() { return fixed; }
}
