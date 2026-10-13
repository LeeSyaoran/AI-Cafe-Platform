package com.example.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReservationStatusRequest {

    @NotBlank(message = "status is required")
    private String status; // confirmed, checked_in, completed, cancelled, no_show

    private String notes;
}
