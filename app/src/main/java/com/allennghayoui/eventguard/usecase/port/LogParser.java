package com.allennghayoui.eventguard.usecase.port;

import com.allennghayoui.eventguard.domain.LogEvent;

public interface LogParser {
    LogEvent parse(String rawLine, String source);
}
