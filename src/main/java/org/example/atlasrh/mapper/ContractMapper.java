package org.example.atlasrh.mapper;

import org.example.atlasrh.dto.ContractResponseDTO;
import org.example.atlasrh.model.Contract;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ContractMapper{
    ContractResponseDTO toResponseDTO(Contract contract);
}
