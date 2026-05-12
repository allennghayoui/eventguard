package com.allennghayoui.eventguard.infrastructure.web.dto;

import static com.allennghayoui.eventguard.infrastructure.web.InputLimits.MAX_RAW_LINE_LENGTH;
import static com.allennghayoui.eventguard.infrastructure.web.InputLimits.MAX_SOURCE_LENGTH;
import static com.allennghayoui.eventguard.infrastructure.web.InputLimits.SOURCE_PATTERN;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record IngestLogLineRequest(
    @NotBlank
    @Size(max = MAX_RAW_LINE_LENGTH, message = "rawLine exceeds max length")
    String rawLine,

    @NotBlank
    @Size(max = MAX_SOURCE_LENGTH, message = "source exceeds max length")
    @Pattern(regexp = SOURCE_PATTERN, message = "source must be alphanumeric, dash, underscore, or dot")
    String source
) {}
