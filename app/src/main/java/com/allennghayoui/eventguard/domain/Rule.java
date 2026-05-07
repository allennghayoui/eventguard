package com.allennghayoui.eventguard.domain;

public interface Rule {
    String name();
    Severity severity();
    boolean matches(LogEvent event);
}
