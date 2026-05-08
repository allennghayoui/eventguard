package com.allennghayoui.eventguard.infrastructure.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.allennghayoui.eventguard.domain.Alert;
import com.allennghayoui.eventguard.infrastructure.web.dto.AlertResponse;
import com.allennghayoui.eventguard.usecase.ListAlerts;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {
    private final ListAlerts listAlerts;

    public AlertController(ListAlerts listAlerts) {
        this.listAlerts = listAlerts;
    }

    @GetMapping
    public ResponseEntity<List<AlertResponse>> list(@RequestParam(required=false) String ruleName) {
        List<Alert> alert = (ruleName == null) ? listAlerts.all() : listAlerts.byRuleName(ruleName);

        List<AlertResponse> body = alert.stream().map(AlertResponse::fromDomain).toList();

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }
}
