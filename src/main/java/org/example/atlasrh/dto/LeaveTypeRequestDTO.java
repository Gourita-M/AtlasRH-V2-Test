package org.example.atlasrh.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.example.atlasrh.model.LeaveType;

public record LeaveTypeRequestDTO(
        @NotBlank String name,
        @NotNull @PositiveOrZero Long annualallowancedays
) {
    public LeaveType toLeaveType() {
        return new LeaveType(name, annualallowancedays);
    }
}
