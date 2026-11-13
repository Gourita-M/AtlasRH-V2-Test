package org.example.atlasrh.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.example.atlasrh.model.Contract;

import java.time.LocalDate;

public record ContractRequestDTO(
        @NotNull Contract.ContractType contractType,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        @NotNull @Positive Long salary
) {
    public Contract toContract() {
        return new Contract(contractType, startDate, endDate, salary);
    }
}
