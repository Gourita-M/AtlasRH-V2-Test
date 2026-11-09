package org.example.atlasrh.service;

import org.example.atlasrh.dto.EmployeeResponseDTO;
import org.example.atlasrh.model.Employee;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {

    Employee create(Employee dto, Long departmentId);

    EmployeeResponseDTO update(Long id, Employee employee);

    Employee viewEmployee(Long id);

    List<EmployeeResponseDTO> viewAll();

    List<EmployeeResponseDTO> viewAllByManager(Long managerId);

    List<EmployeeResponseDTO> searchForEmployee(String name, Long departmentId, Pageable pageable);

}