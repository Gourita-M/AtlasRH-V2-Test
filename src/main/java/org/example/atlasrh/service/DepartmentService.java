package org.example.atlasrh.service;

import org.example.atlasrh.dto.DepartmentResponseDTO;
import org.example.atlasrh.model.Department;
import org.example.atlasrh.model.Employee;

import java.util.List;

public interface DepartmentService {

    DepartmentResponseDTO create(Department department);

    DepartmentResponseDTO update(Long id, Employee employee);

    Department viewDepartment(Long id);

    List<DepartmentResponseDTO> viewAll();

    List<DepartmentResponseDTO> viewAllByManager(Long managerId);

    boolean deleteDepartment(Long id);
}
