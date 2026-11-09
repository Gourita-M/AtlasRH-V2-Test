package org.example.atlasrh.service;

import org.example.atlasrh.dto.DepartmentResponseDTO;
import org.example.atlasrh.mapper.DepartmentMapper;
import org.example.atlasrh.model.Department;
import org.example.atlasrh.model.Employee;
import org.example.atlasrh.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository,  DepartmentMapper departmentMapper) {
        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper;
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
//        List<DepartmentResponseDTO> departments = departmentRepository.findAll().stream()
//                .filter(d -> d.getEmployees())
        return null;
    }

    @Override
    public boolean deleteDepartment(Long id){

        Department department = departmentRepository.findById(id).orElseThrow( () -> new RuntimeException("There is no Department with this ID"));
        departmentRepository.delete(department);
        return true;
    }
}
