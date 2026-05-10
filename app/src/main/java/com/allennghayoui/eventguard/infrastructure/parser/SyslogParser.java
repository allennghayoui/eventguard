package com.allennghayoui.eventguard.infrastructure.parser;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.allennghayoui.eventguard.domain.LogEvent;
import com.allennghayoui.eventguard.domain.Severity;
import com.allennghayoui.eventguard.infrastructure.parser.SyslogPriorityDecoder.DecodedPriority;
import com.allennghayoui.eventguard.usecase.port.LogParser;

/**
 * Parses RFC 3164 syslog messages. Handles:
 *  - Optional priority code <N>
 *  - Standard timestamp: "MMM d HH:mm:ss" (year inferred as current)
 *  - Hostname and program (with optional PID)
 *  - Message body
 * 
 * Falls back gracefully on unparseable input - the raw line becomes
 * the message, parseError is recorded in fields, severity defaults
 * to INFO. The system stays useful even when the parser doesn't
 * fully understand a line.
 */
public class SyslogParser implements LogParser {
    private static final Pattern WITH_PRIORITY = Pattern.compile(
        "^<(\\d{1,3})>\\s*(.*)$"
    );

    private static final Pattern RFC_3164 = Pattern.compile(
        "^([A-Za-z]{3})\\s+(\\d{1,2})\\s+(\\d{2}):(\\d{2}):(\\d{2})" +
        "\\s+(\\S+)" +                          // hostname
        "\\s+(\\S+?)(?:\\[(\\d+)\\])?:" +        // program[pid]:
        "\\s*(.*)$"
    );

    private static final DateTimeFormatter MONTH_PARSER = DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH);

    @Override
    public LogEvent parse(String rawLine, String source) {
        String line = rawLine == null ? "" : rawLine.trim();
        Map<String, String> fields = new HashMap<>();
        fields.put("raw", rawLine == null ? "" : rawLine); // preserve original rawLine
        Severity severity = Severity.INFO;

        // Step 1: extract priority if present
        Matcher priorityMatcher = WITH_PRIORITY.matcher(line);
        if (priorityMatcher.matches()) {
            try {
                int priority = Integer.parseInt(priorityMatcher.group(1));

                if (priority >= 0 && priority <= 191) {
                    DecodedPriority decoded = SyslogPriorityDecoder.decode(priority);
                    severity = decoded.severity();
                    fields.put("syslog.facility", String.valueOf(decoded.facility()));
                    fields.put("syslog.severity", String.valueOf(decoded.severityNumber()));
                }

                line = priorityMatcher.group(2);
            } catch (NumberFormatException e) {
                // priority claimed but not a number - leave the line alone
            }
        }

        // Step 2: parse the body
        Matcher bodyMatcher = RFC_3164.matcher(line);
        if (!bodyMatcher.matches()) {
            fields.put("parseError", "rfc3164-no-match");
            return LogEvent.create(Instant.now(), source, severity, line, fields);
        }

        String monthAbbreviation = bodyMatcher.group(1);
        String day = bodyMatcher.group(2);
        String hour = bodyMatcher.group(3);
        String minute = bodyMatcher.group(4);
        String second = bodyMatcher.group(5);

        Instant timestamp = parseTimestamp(
            monthAbbreviation, day,
            hour, minute, second,
            fields
        );

        String hostname = bodyMatcher.group(6);
        String program = bodyMatcher.group(7);
        String pid = bodyMatcher.group(8);
        String message = bodyMatcher.group(9);

        fields.put("hostname", hostname);
        fields.put("program", program);
        if (pid != null) fields.put("pid", pid);

        // Bump severity for failure-shaped messages if priority didn't tell us
        if (severity == Severity.INFO) {
            String lower = message.toLowerCase();
            if (lower.contains("error") || lower.contains("failed") || lower.contains("denied")) {
                severity = Severity.WARNING;
            }
        }

        return LogEvent.create(timestamp, source, severity, message, fields);
    }

    private Instant parseTimestamp(
        String monthAbbreviation,
        String day, 
        String h, 
        String m, 
        String s, 
        Map<String, String> fields
    ) {
        try {
            Month month = Month.from(MONTH_PARSER.parse(monthAbbreviation));
            int dayOfMonth = Integer.parseInt(day);
            int hour = Integer.parseInt(h);
            int minute = Integer.parseInt(m);
            int second = Integer.parseInt(s);

            int currentYear = LocalDateTime.now(ZoneOffset.UTC).getYear();
            return LocalDateTime.of(currentYear, month, dayOfMonth, hour, minute, second)
                .toInstant(ZoneOffset.UTC);
        } catch (NumberFormatException | DateTimeException e) {
            fields.put("parseError", "timestamp:" + e.getClass().getSimpleName());
            return Instant.now();
        }
    }
}
