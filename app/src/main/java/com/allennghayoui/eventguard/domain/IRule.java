package com.allennghayoui.eventguard.domain;

public interface IRule {
    String name();
    Severity severity();
    boolean matches(LogEvent event);
}
