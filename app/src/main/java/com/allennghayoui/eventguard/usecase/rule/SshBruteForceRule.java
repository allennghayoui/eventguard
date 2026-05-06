package com.allennghayoui.eventguard.usecase.rule;

import com.allennghayoui.eventguard.domain.IRule;
import com.allennghayoui.eventguard.domain.LogEvent;
import com.allennghayoui.eventguard.domain.Severity;

public class SshBruteForceRule implements IRule {
    @Override
    public String name() {
        return "ssh-bruteforce";
    }

    @Override
    public Severity severity() {
        return Severity.CRITICAL;
    }

    @Override
    public boolean matches(LogEvent event) {
        if (!"syslog".equals(event.source())) return false;
        String message = event.message().toLowerCase();
        return message.contains("failed password") && message.contains("sshd");
    }
}
