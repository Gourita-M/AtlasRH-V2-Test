package org.example.atlasrh;

import org.example.atlasrh.model.Contract;
import org.example.atlasrh.model.Department;
import org.example.atlasrh.model.Employee;
import org.example.atlasrh.Enum.RoleType;
import org.example.atlasrh.dto.EmployeeResponseDTO;
import org.example.atlasrh.mapper.EmployeeMapper;
import org.example.atlasrh.repository.ContractRepository;
import org.example.atlasrh.repository.EmployeeRepository;
import org.example.atlasrh.service.DepartmentService;
import org.example.atlasrh.service.EmployeeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private EmployeeMapper employeeMapper;
    @Mock
    private DepartmentService departmentService;
    @Mock
    private ContractRepository contractRepository;
    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Test
    void shouldCreateEmployeeWithUniqueEmailAndExistingManager() {
        Employee employee = employee(0, "new@atlasrh.test");
        Employee manager = employee(7, "manager@atlasrh.test");
        employee.setManager(manager);
        Department department = new Department("HR", "People");
        Contract contract = new Contract();

        when(employeeRepository.existsByEmail(employee.getEmail())).thenReturn(false);
        when(employeeRepository.findById(7L)).thenReturn(Optional.of(manager));
        when(departmentService.viewDepartment(2L)).thenReturn(department);
        when(contractRepository.findById(3L)).thenReturn(Optional.of(contract));
        when(employeeRepository.save(employee)).thenReturn(employee);

        Employee created = employeeService.create(employee, 2L, 3L);

        assertSame(department, created.getDepartment());
        assertSame(contract, created.getContract());
        assertSame(manager, created.getManager());
        verify(employeeRepository).save(employee);
    }

    @Test
    void shouldRejectDuplicateEmployeeNumber() {
        Employee employee = employee(12, "new@atlasrh.test");
        when(employeeRepository.existsById(12L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> employeeService.create(employee, 2L, 3L));
        verifyNoInteractions(departmentService, contractRepository);
    }

    @Test
    void shouldRejectDuplicateEmail() {
        Employee employee = employee(0, "duplicate@atlasrh.test");
        when(employeeRepository.existsByEmail(employee.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> employeeService.create(employee, 2L, 3L));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void shouldRejectEmployeeAsOwnManager() {
        Employee employee = employee(12, "employee@atlasrh.test");
        employee.setManager(employee);

        assertThrows(IllegalArgumentException.class,
                () -> employeeService.create(employee, 2L, 3L));
        verifyNoInteractions(departmentService, contractRepository);
    }

    @Test
    void shouldViewEmployeeAndMapEmployeeCollections() {
        Employee employee = employee(7, "ada@atlasrh.test");
        EmployeeResponseDTO dto = new EmployeeResponseDTO(7L, "Ada", "Lovelace",
                employee.getEmail(), "Analyst", employee.getHireDate(), "RH_MANAGER");
        when(employeeRepository.findById(7L)).thenReturn(Optional.of(employee));
        when(employeeRepository.findAll()).thenReturn(List.of(employee));
        when(employeeRepository.findAllByManager_Matricule(8L)).thenReturn(List.of(employee));
        when(employeeMapper.toResponseDTO(employee)).thenReturn(dto);

        assertSame(employee, employeeService.viewEmployee(7L));
        assertEquals(List.of(dto), employeeService.viewAll());
        assertEquals(List.of(dto), employeeService.viewAllByManager(8L));
        assertEquals(List.of(dto), employeeService.getEmplyeeByMart(7L));
        verify(employeeRepository).findAllByManager_Matricule(8L);
    }

    @Test
    void shouldThrowWhenEmployeeDoesNotExist() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> employeeService.viewEmployee(99L));
    }

    @Test
    void shouldSearchAndUpdateEmployee() {
        Employee employee = employee(7, "ada@atlasrh.test");
        Employee changes = employee(0, "ada@atlasrh.test");
        EmployeeResponseDTO dto = new EmployeeResponseDTO(7L, "Ada", "Lovelace",
                employee.getEmail(), "Analyst", employee.getHireDate(), "RH_MANAGER");
        when(employeeRepository.searchEmployees("ada", 2L, PageRequest.of(0, 5)))
                .thenReturn(new PageImpl<>(List.of(employee)));
        when(employeeMapper.toResponseDTO(employee)).thenReturn(dto);
        when(employeeRepository.findById(7L)).thenReturn(Optional.of(employee));
        when(employeeRepository.existsByEmailAndMatriculeNot("ada@atlasrh.test", 7L))
                .thenReturn(false);
        when(employeeRepository.save(employee)).thenReturn(employee);

        assertEquals(List.of(dto), employeeService.searchForEmployee(
                " ada ", 2L, PageRequest.of(0, 5)));
        assertEquals(dto, employeeService.update(7L, changes));
        assertEquals("ada@atlasrh.test", employee.getEmail());
    }

    @Test
    void shouldRejectUpdatingEmployeeToBeTheirOwnManager() {
        Employee existing = employee(7, "ada@atlasrh.test");
        Employee changes = employee(0, "ada@atlasrh.test");
        changes.setManager(employee(7, "ada@atlasrh.test"));
        when(employeeRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(employeeRepository.existsByEmailAndMatriculeNot("ada@atlasrh.test", 7L))
                .thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> employeeService.update(7L, changes));
        verify(employeeRepository, never()).save(any());
    }

    private Employee employee(long matricule, String email) {
        Employee employee = new Employee("Ada", "Lovelace", email, "Analyst",
                LocalDate.of(2020, 1, 1), RoleType.RH_MANAGER);
        employee.setMatricule(matricule);
        return employee;
    }
}
