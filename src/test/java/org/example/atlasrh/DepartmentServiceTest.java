package org.example.atlasrh;

import org.example.atlasrh.dto.DepartmentResponseDTO;
import org.example.atlasrh.mapper.DepartmentMapper;
import org.example.atlasrh.model.Department;
import org.example.atlasrh.model.Employee;
import org.example.atlasrh.repository.DepartmentRepository;
import org.example.atlasrh.repository.EmployeeRepository;
import org.example.atlasrh.service.DepartmentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private DepartmentMapper departmentMapper;
    @Mock
    private EmployeeRepository employeeRepository;
    @InjectMocks
    private DepartmentServiceImpl departmentService;

    @Test
    void createsAndListsDepartments() {
        Department department = new Department("People", "HR");
        DepartmentResponseDTO dto = new DepartmentResponseDTO(1L, "People", "HR");
        when(departmentRepository.save(department)).thenReturn(department);
        when(departmentRepository.findAll()).thenReturn(List.of(department));
        when(departmentMapper.toResponseDTO(department)).thenReturn(dto);

        assertEquals(dto, departmentService.create(department));
        assertEquals(List.of(dto), departmentService.viewAll());
    }

    @Test
    void returnsDepartmentOrNullWhenMissing() {
        Department department = new Department("People", "HR");
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(departmentRepository.findById(2L)).thenReturn(Optional.empty());

        assertSame(department, departmentService.viewDepartment(1L));
        assertNull(departmentService.viewDepartment(2L));
    }

    @Test
    void listsDepartmentsForManager() {
        Department department = new Department("People", "HR");
        Employee employee = new Employee();
        employee.setDepartment(department);
        DepartmentResponseDTO dto = new DepartmentResponseDTO(1L, "People", "HR");
        when(employeeRepository.findAllByManager_Matricule(7L)).thenReturn(List.of(employee));
        when(departmentMapper.toResponseDTO(department)).thenReturn(dto);

        assertEquals(List.of(dto), departmentService.viewAllByManager(7L));
    }

    @Test
    void deletesEmptyDepartmentAndRejectsMissingOrOccupiedDepartment() {
        Department department = new Department("People", "HR");
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(employeeRepository.existsByDepartment_Id(1L)).thenReturn(false);
        when(departmentRepository.findById(2L)).thenReturn(Optional.of(department));
        when(employeeRepository.existsByDepartment_Id(2L)).thenReturn(true);
        when(departmentRepository.findById(3L)).thenReturn(Optional.empty());

        assertTrue(departmentService.deleteDepartment(1L));
        assertThrows(IllegalStateException.class, () -> departmentService.deleteDepartment(2L));
        assertThrows(RuntimeException.class, () -> departmentService.deleteDepartment(3L));
        verify(departmentRepository).delete(department);
        verify(departmentRepository, times(1)).delete(department);
    }
}
