package com.allennghayoui.eventguard.usecase.rule;

import com.allennghayoui.eventguard.domain.LogEvent;
import com.allennghayoui.eventguard.domain.Rule;
import com.allennghayoui.eventguard.domain.Severity;

public class SshBruteForceRule implements Rule {
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
        if (!"sshd".equals(event.fields().get("program"))) return false;
        return event.message().toLowerCase().contains("failed password");
    }
}
