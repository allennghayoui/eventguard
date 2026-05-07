package com.allennghayoui.eventguard.usecase.rule;

import org.springframework.stereotype.Component;

import com.allennghayoui.eventguard.domain.LogEvent;
import com.allennghayoui.eventguard.domain.Rule;
import com.allennghayoui.eventguard.domain.Severity;

@Component
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
        String message = event.message().toLowerCase();
        return message.contains("failed password") && message.contains("sshd");
    }
}
