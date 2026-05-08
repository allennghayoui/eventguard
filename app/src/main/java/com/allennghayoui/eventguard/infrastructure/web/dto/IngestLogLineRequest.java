package com.allennghayoui.eventguard.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

public record IngestLogLineRequest(
    @NotBlank String rawLine,
    @NotBlank String source
) {}
