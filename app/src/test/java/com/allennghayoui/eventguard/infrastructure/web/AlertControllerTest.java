package com.allennghayoui.eventguard.infrastructure.web;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.allennghayoui.eventguard.domain.Alert;
import com.allennghayoui.eventguard.domain.Severity;
import com.allennghayoui.eventguard.usecase.ListAlerts;

@WebMvcTest(AlertController.class)
public class AlertControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ListAlerts listAlerts;

    @Test
    void listReturnsAllAlerts() throws Exception {
        Alert alert1 = new Alert(
            UUID.randomUUID(),
            "rule-a",
            UUID.randomUUID(),
            Severity.WARNING,
            Instant.now()
        );

        Alert alert2 = new Alert(
            UUID.randomUUID(),
            "rule-b",
            UUID.randomUUID(),
            Severity.CRITICAL,
            Instant.now()
        );

        when(listAlerts.all()).thenReturn(List.of(alert1, alert2));

        mockMvc.perform(get("/api/alerts"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));
    }
}
