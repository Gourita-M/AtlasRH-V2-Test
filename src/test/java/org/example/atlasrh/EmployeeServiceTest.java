package org.example.atlasrh;

import org.example.atlasrh.model.Department;
import org.example.atlasrh.model.Employee;
import org.example.atlasrh.repository.DepartmentRepository;
import org.example.atlasrh.repository.EmployeeRepository;
import org.example.atlasrh.service.DepartmentService;
import org.example.atlasrh.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentService departmentService;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void CreateEmployeeSuccessfully(){
        Employee employee = new Employee();
        Department department = new Department();
        Long departmentId = 1L;

        when(departmentService.viewDepartment(departmentId))
                .thenReturn(department);

        when(employeeRepository.save(employee))
                .thenReturn(employee);

        Employee result = employeeService.create(employee, departmentId);

        assertNotNull(result);
        assertEquals(department, result.getDepartment());

        verify(employeeRepository).save(employee);
    }
}
