package com.allennghayoui.eventguard.infrastructure.web;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.allennghayoui.eventguard.domain.LogEvent;
import com.allennghayoui.eventguard.domain.NotFoundException;
import com.allennghayoui.eventguard.domain.Severity;
import com.allennghayoui.eventguard.usecase.IngestLogLine;
import com.allennghayoui.eventguard.usecase.SearchLogEvents;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(LogEventController.class)
@Import(GlobalExceptionHandler.class)
public class LogEventControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IngestLogLine ingestLogLine;

    @MockitoBean
    private SearchLogEvents searchLogEvents;

    @Test
    void ingestReturns201WithCreatedEvent() throws Exception {
        LogEvent event = LogEvent.create(
            Instant.parse("2025-01-01T00:00:00Z"),
            "syslog",
            Severity.WARNING,
            "test message",
            Map.of()
        );

        when(ingestLogLine.execute(anyString(), anyString())).thenReturn(event);

        String requestBody = objectMapper.writeValueAsString(
            Map.of("rawLine", "some line", "source", "syslog")
        );

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.message").value("test message"))
            .andExpect(jsonPath("$.severity").value("WARNING"));
    }

    @Test
    void ingestReturns400OnBlankInput() throws Exception {
        String requestBody = objectMapper.writeValueAsString(
            Map.of("rawLine", "", "source", "")
        );

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Validation failed"))
            .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void findByIdReturns404WhenMissing() throws Exception {
        UUID id = UUID.randomUUID();

        when(searchLogEvents.byId(id)).thenThrow(new NotFoundException("not found"));

        mockMvc.perform(get("/api/events/" + id))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }
}
