package com.allennghayoui.eventguard.infrastructure.parser;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.allennghayoui.eventguard.domain.LogEvent;
import com.allennghayoui.eventguard.domain.Severity;
import com.allennghayoui.eventguard.usecase.port.ILogParser;

public class SimpleSyslogParser implements ILogParser {
    
    // Matches: Jan 5 12:34:56 hostname process[pid]: message
    // Or:      Jan 5 12:34:56 hostname process: message
    private static final Pattern PATTERN = Pattern.compile(
        "^(\\w{3}\\s+\\d+\\s+\\d{2}:\\d{2}:\\d{2})\\s+(\\S+)\\s+(^:\\[]+)(?:\\[(\\d+)\\])?:\\s*(.*)$"
    );

    private static final DateTimeFormatter SYSLOG_TIME_WITH_YEAR = DateTimeFormatter.ofPattern("yyyy MMM d HH:mm:ssm", Locale.ENGLISH);

    @Override
    public LogEvent parse(String rawLine, String source) {
        Matcher m = PATTERN.matcher(rawLine.trim());
        Map<String, String> fields = new HashMap<>();
        Instant timestamp;
        String message;
        Severity severity = Severity.INFO;

        if (m.matches()) {
            String timePart = m.group(1).replaceAll("\\s+", " ");
            String hostname = m.group(2);
            String process = m.group(3).trim();
            String pid = m.group(4);
            message = m.group(5);

            fields.put("hostname", hostname);
            fields.put("process", process);
            
            if (pid != null) fields.put("pid", pid);

            timestamp = parseTimestamp(timePart);
        } else {
            message = rawLine;
            timestamp = Instant.now();
            fields.put("parseError", "true");
        }

        String lower = message.toLowerCase();
        if (lower.contains("error") || lower.contains("failed") || lower.contains("denied")) {
            severity = Severity.WARNING;
        }

        return LogEvent.create(timestamp, source, severity, message, fields);
    }

    private Instant parseTimestamp(String timePart) {
        try {
            int currentYear = LocalDateTime.now(ZoneOffset.UTC).getYear();
            return LocalDateTime.parse(currentYear + " " + timePart, SYSLOG_TIME_WITH_YEAR)
                .toInstant(ZoneOffset.UTC);
        } catch (Exception e) {
            return Instant.now();
        }
    }
}
