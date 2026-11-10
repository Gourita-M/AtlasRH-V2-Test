package org.example.atlasrh.dto;

public record LeaveBalanceResponseDTO(
        Integer leaveTypeId,
        String leaveTypeName,
        Long annualAllowanceDays,
        long approvedDaysUsed,
        long remainingDays
) {
}
