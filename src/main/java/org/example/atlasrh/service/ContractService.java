package org.example.atlasrh.service;

import org.example.atlasrh.dto.ContractResponseDTO;
import org.example.atlasrh.model.Contract;

import java.util.List;

public interface ContractService {

    ContractResponseDTO create(Contract contract, Long id);

    ContractResponseDTO update(Long id, Contract contract);

    ContractResponseDTO viewContract(Long id);

    List<ContractResponseDTO> viewAll();

    boolean endContract(Long contractId);

    List<ContractResponseDTO> getActiveContracts(Long employeeId);
}
