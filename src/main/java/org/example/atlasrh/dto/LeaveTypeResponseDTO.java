package org.example.atlasrh.dto;

public record LeaveTypeResponseDTO(
        Integer id,
        String name,
        Long annualallowancedays
) {
}
