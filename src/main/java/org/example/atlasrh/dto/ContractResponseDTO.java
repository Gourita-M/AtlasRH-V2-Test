package org.example.atlasrh.dto;

import java.time.LocalDate;

public record ContractResponseDTO(
        Long id,
        String contractType,
        LocalDate startDate,
        LocalDate endDate,
        Long salary
) {

}
