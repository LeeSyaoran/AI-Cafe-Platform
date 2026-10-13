package com.example.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {
    private UUID id;
    private UUID planId;
    private String planName;
    private String status;
    private String billingCycle;
    private Instant startDate;
    private Instant endDate;
    private Integer creditsRemaining;
    private Integer creditsTotal;
    private Boolean autoRenew;
}
