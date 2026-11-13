package org.example.atlasrh;

import org.example.atlasrh.dto.ContractResponseDTO;
import org.example.atlasrh.mapper.ContractMapper;
import org.example.atlasrh.model.Contract;
import org.example.atlasrh.model.Employee;
import org.example.atlasrh.repository.ContractRepository;
import org.example.atlasrh.service.ContractServiceImpl;
import org.example.atlasrh.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContractServiceTest {
    @Mock
    private ContractRepository contractRepository;
    @Mock
    private ContractMapper contractMapper;
    @Mock
    private EmployeeService employeeService;
    @InjectMocks
    private ContractServiceImpl contractService;

    @Test
    void shouldCreateContractWhenEmployeeHasNoActiveContract() {
        Employee employee = new Employee();
        employee.setMatricule(1L);
        Contract contract = contract(LocalDate.now().minusDays(1), LocalDate.now().plusDays(30));
        ContractResponseDTO response = new ContractResponseDTO(1L, "CDI",
                contract.getStartDate(), contract.getEndDate(), 50_000L);
        when(employeeService.viewEmployee(1L)).thenReturn(employee);
        when(contractRepository.save(contract)).thenReturn(contract);
        when(contractMapper.toResponseDTO(contract)).thenReturn(response);

        assertEquals(response, contractService.create(contract, 1L));
        assertSame(contract, employee.getContract());
        verify(employeeService).update(1L, employee);
    }

    @Test
    void shouldRejectSecondActiveContract() {
        Employee employee = new Employee();
        employee.setContract(contract(LocalDate.now().minusDays(5), LocalDate.now().plusDays(5)));
        Contract nextContract = contract(LocalDate.now(), LocalDate.now().plusDays(30));
        when(employeeService.viewEmployee(1L)).thenReturn(employee);

        assertThrows(IllegalArgumentException.class,
                () -> contractService.create(nextContract, 1L));
        verifyNoInteractions(contractRepository);
    }

    @Test
    void shouldRejectContractWithEndDateBeforeStartDate() {
        Employee employee = new Employee();
        when(employeeService.viewEmployee(1L)).thenReturn(employee);
        Contract invalidContract = contract(LocalDate.now().plusDays(2), LocalDate.now());

        assertThrows(IllegalArgumentException.class,
                () -> contractService.create(invalidContract, 1L));
        verifyNoInteractions(contractRepository);
    }

    @Test
    void shouldUpdateViewEndAndListContracts() {
        Contract original = contract(LocalDate.now().minusDays(2), LocalDate.now().plusDays(20));
        Contract changed = contract(LocalDate.now().minusDays(1), LocalDate.now().plusDays(30));
        ContractResponseDTO response = new ContractResponseDTO(1L, "CDI",
                changed.getStartDate(), changed.getEndDate(), 50_000L);
        when(contractRepository.findById(1L)).thenReturn(Optional.of(original));
        when(contractRepository.findById(2L)).thenReturn(Optional.empty());
        when(contractRepository.save(changed)).thenReturn(changed);
        when(contractRepository.findAll()).thenReturn(List.of(changed));
        when(contractMapper.toResponseDTO(changed)).thenReturn(response);

        assertEquals(response, contractService.update(1L, changed));
        assertNull(contractService.update(2L, changed));
        assertSame(original, contractService.viewContract(1L));
        assertNull(contractService.viewContract(2L));
        assertEquals(List.of(response), contractService.viewAll());
        assertTrue(contractService.endContract(1L));
        assertFalse(contractService.endContract(2L));
        verify(contractRepository).delete(original);
    }

    @Test
    void returnsOnlyCurrentlyActiveEmployeeContract() {
        Employee employee = new Employee();
        Contract active = contract(LocalDate.now(), LocalDate.now().plusDays(1));
        employee.setContract(active);
        ContractResponseDTO response = new ContractResponseDTO(1L, "CDI",
                active.getStartDate(), active.getEndDate(), 50_000L);
        when(employeeService.viewEmployee(1L)).thenReturn(employee);
        when(contractMapper.toResponseDTO(active)).thenReturn(response);

        assertEquals(List.of(response), contractService.getActiveContracts(1L));

        employee.setContract(contract(LocalDate.now().minusDays(2), LocalDate.now().minusDays(1)));
        assertTrue(contractService.getActiveContracts(1L).isEmpty());
    }

    private Contract contract(LocalDate startDate, LocalDate endDate) {
        return new Contract(Contract.ContractType.CDI, startDate, endDate, 50_000L);
    }
}
