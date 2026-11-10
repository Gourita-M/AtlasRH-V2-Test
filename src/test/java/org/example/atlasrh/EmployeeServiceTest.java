package org.example.atlasrh;

import org.example.atlasrh.mapper.EmployeeMapper;
import org.example.atlasrh.model.Contract;
import org.example.atlasrh.model.Department;
import org.example.atlasrh.model.Employee;
import org.example.atlasrh.repository.ContractRepository;
import org.example.atlasrh.repository.EmployeeRepository;
import org.example.atlasrh.service.DepartmentService;
import org.example.atlasrh.service.EmployeeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

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
    void CreateEmployeeSuccessfully(){
        Employee employee = new Employee();
        Department department = new Department();
        Contract contract = new Contract();
        Long departmentId = 1L;
        Long contractId = 1L;

        when(departmentService.viewDepartment(departmentId))
                .thenReturn(department);
        when(contractRepository.findById(contractId))
                .thenReturn(Optional.of(contract));

        when(employeeRepository.save(employee))
                .thenReturn(employee);

        Employee result = employeeService.create(employee, departmentId, contractId);

        assertNotNull(result);
        assertEquals(department, result.getDepartment());
        assertEquals(contract, result.getContract());

        verify(employeeRepository).save(employee);
    }
}
