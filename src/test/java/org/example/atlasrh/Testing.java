package org.example.atlasrh;


import org.example.atlasrh.dto.DepartmentResponseDTO;
import org.example.atlasrh.mapper.DepartmentMapper;
import org.example.atlasrh.model.Department;
import org.example.atlasrh.repository.DepartmentRepository;
import org.example.atlasrh.repository.EmployeeRepository;
import org.example.atlasrh.service.DepartmentServiceImpl;
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
public class Testing {

    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private DepartmentMapper departmentMapper;
    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    @Test
    void shouldCreateDepartment(){

        Department department = new Department();
        DepartmentResponseDTO dto = new DepartmentResponseDTO(1L,"ahah","fkjel");

        when(departmentRepository.save(department))
                .thenReturn(department);
        when(departmentMapper.toResponseDTO(department))
                .thenReturn(dto);

        DepartmentResponseDTO result = departmentService.create(department);
        assertNotNull(result);
        assertEquals(dto, result);

        verify(departmentRepository).save(department);
        verify(departmentMapper).toResponseDTO(department);
    }
}
