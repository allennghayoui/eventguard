package com.allennghayoui.eventguard.infrastructure.parser;

import com.allennghayoui.eventguard.domain.Severity;

/**
 * Decodes RFC 3164 syslog priority codes (e.g., <13>) into facility and severity.
 * priority = facility * 8 + severity
 */
public class SyslogPriorityDecoder {
    private SyslogPriorityDecoder() {}

    record DecodedPriority(int facility, int severityNumber, Severity severity) {}

    static DecodedPriority decode(int priority) {
        int facility = priority / 8;
        int severity = priority % 8;

        return new DecodedPriority(facility, severity, mapSeverity(severity));
    }

    private static Severity mapSeverity(int syslogSeverity) {
        return switch (syslogSeverity) {
            case 0, 1, 2 -> Severity.CRITICAL;
            case 3 -> Severity.ERROR;
            case 4 -> Severity.WARNING;
            case 5, 6 -> Severity.INFO;
            case 7 -> Severity.DEBUG;
            default -> Severity.INFO;
        };
    }
}
