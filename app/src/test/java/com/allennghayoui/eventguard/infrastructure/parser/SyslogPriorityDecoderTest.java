package com.allennghayoui.eventguard.infrastructure.parser;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import com.allennghayoui.eventguard.domain.Severity;
import com.allennghayoui.eventguard.infrastructure.parser.SyslogPriorityDecoder.DecodedPriority;

public class SyslogPriorityDecoderTest {
    
    @Test
    void decodesFacilityAndSeverity() {
        // priority 13 = facility 1 * 8 + severity 5
        DecodedPriority decoded = SyslogPriorityDecoder.decode(13);

        assertThat(decoded.facility()).isEqualTo(1);
        assertThat(decoded.severityNumber()).isEqualTo(5);
    }
    
    @Test
    void mapsEmergencyToCritical() {
        assertThat(SyslogPriorityDecoder.decode(0).severity()).isEqualTo(Severity.CRITICAL);
        assertThat(SyslogPriorityDecoder.decode(1).severity()).isEqualTo(Severity.CRITICAL);
        assertThat(SyslogPriorityDecoder.decode(2).severity()).isEqualTo(Severity.CRITICAL);
    }

    @Test
    void mapsErrorToError() {
        assertThat(SyslogPriorityDecoder.decode(3).severity()).isEqualTo(Severity.ERROR);
    }

    @Test
    void mapsWarningToWarning() {
        assertThat(SyslogPriorityDecoder.decode(4).severity()).isEqualTo(Severity.WARNING);
    }

    @Test
    void mapsNoticeAndInfoToInfo() {
        assertThat(SyslogPriorityDecoder.decode(5).severity()).isEqualTo(Severity.INFO);
        assertThat(SyslogPriorityDecoder.decode(6).severity()).isEqualTo(Severity.INFO);
    }

    @Test
    void mapsDebugToDebug() {
        assertThat(SyslogPriorityDecoder.decode(7).severity()).isEqualTo(Severity.DEBUG);
    }

    @Test
    void handlesMaxPriority() {
        // 191 = facility 23 * 8 + severity 7
        DecodedPriority decoded = SyslogPriorityDecoder.decode(191);

        assertThat(decoded.facility()).isEqualTo(23);
        assertThat(decoded.severityNumber()).isEqualTo(7);
        assertThat(decoded.severity()).isEqualTo(Severity.DEBUG);
    }
}
