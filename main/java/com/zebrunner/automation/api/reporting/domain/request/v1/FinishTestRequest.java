package com.zebrunner.automation.api.reporting.domain.request.v1;

import lombok.*;

import java.time.OffsetDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FinishTestRequest {

    private OffsetDateTime endedAt;
    private String reason;
    private String result;

    public static FinishTestRequest getRequestWithoutReason(String result) {
        return FinishTestRequest.builder()
                .endedAt(OffsetDateTime.now())
                .result(result)
                .build();
    }

    public static FinishTestRequest getRequestWithReason(String reason) {
        return FinishTestRequest.builder()
                .endedAt(OffsetDateTime.now())
                .result("FAILED")
                .reason(reason)
                .build();
    }
}