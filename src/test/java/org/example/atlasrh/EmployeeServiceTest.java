package org.example.atlasrh;

import org.example.atlasrh.controller.EmployeeController;
import org.example.atlasrh.dto.EmployeeResponseDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @Mock
    private EmployeeController employeeController;

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

    @Test
    void GetAllEmployeesSuccessfully(){
        Employee employee1 = new Employee();
        Employee employee2 = new Employee();

        List<Employee> employeeList = List.of(employee1, employee2);

        when(employeeService.viewAll()).thenReturn(employeeList.stream().map(employeeMapper::toResponseDTO).toList());

        ResponseEntity<List<EmployeeResponseDTO>> response = employeeController.getAll();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals(employee1, response.getBody().get(0));
        assertEquals(employee2, response.getBody().get(1));

        verify(employeeService, times(1)).viewAll();
    }
}
