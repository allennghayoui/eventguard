package com.allennghayoui.eventguard.usecase;

import java.time.Instant;

import com.allennghayoui.eventguard.usecase.port.IClock;

public class FixedClock implements IClock {
    private final Instant fixed;
    
    public FixedClock(Instant fixed) { this.fixed = fixed; }

    @Override public Instant now() { return fixed; }
}
