package org.example.atlasrh.service;

import org.example.atlasrh.dto.DepartmentResponseDTO;
import org.example.atlasrh.mapper.DepartmentMapper;
import org.example.atlasrh.model.Department;
import org.example.atlasrh.model.Employee;
import org.example.atlasrh.repository.DepartmentRepository;
import org.example.atlasrh.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;
    private final EmployeeRepository employeeRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository, DepartmentMapper departmentMapper,
                                 EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public DepartmentResponseDTO create(Department department) {
        Department created = departmentRepository.save(department);
        return departmentMapper.toResponseDTO(created);
    }

    @Override
    public DepartmentResponseDTO update(Long id, Employee employee){
        return null;
    }

    @Override
    public Department viewDepartment(Long id){
        return departmentRepository.findById(id).orElse(null);
    }

    @Override
    public List<DepartmentResponseDTO> viewAll(){
        return departmentRepository.findAll()
                .stream().map(departmentMapper::toResponseDTO)
                .toList();

    }

    @Override
    public List<DepartmentResponseDTO> viewAllByManager(Long managerId){
        return employeeRepository.findAllByManager_Matricule(managerId).stream()
                .map(Employee::getDepartment)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .map(departmentMapper::toResponseDTO)
                .toList();
    }

    @Override
    public boolean deleteDepartment(Long id){

        Department department = departmentRepository.findById(id).orElseThrow( () -> new RuntimeException("There is no Department with this ID"));
        if (employeeRepository.existsByDepartment_Id(id)) {
            throw new IllegalStateException("Cannot delete a department that has employees");
        }
        departmentRepository.delete(department);
        return true;
    }
}
