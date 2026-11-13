package org.example.atlasrh.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.atlasrh.model.LeaveRequest;

import java.time.LocalDate;

public record LeaveRequestRequestDTO(
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        @NotBlank String reason
) {
    public LeaveRequest toLeaveRequest() {
        return new LeaveRequest(startDate, endDate, reason, null);
    }
}
