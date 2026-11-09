package org.example.atlasrh.dto;

import java.time.LocalDate;

public record LeaveRequestResponseDTO(
        Long id,
        LocalDate startDate,
        LocalDate endDate,
        String reason
) {
}
