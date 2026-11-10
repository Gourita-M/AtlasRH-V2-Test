package org.example.atlasrh.service;

import org.example.atlasrh.Enum.RoleType;
import org.example.atlasrh.dto.EmployeeResponseDTO;
import org.example.atlasrh.mapper.EmployeeMapper;
import org.example.atlasrh.model.Contract;
import org.example.atlasrh.model.Department;
import org.example.atlasrh.model.Employee;
import org.example.atlasrh.repository.ContractRepository;
import org.example.atlasrh.repository.EmployeeRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final DepartmentService departmentService;
    private final ContractRepository contractRepository;

    public EmployeeServiceImpl(
            EmployeeRepository employeeRepository,
            EmployeeMapper employeeMapper, 
            DepartmentService departmentService,
            ContractRepository contractRepository) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
        this.departmentService = departmentService;
        this.contractRepository = contractRepository;
    }

    @Override
    public Employee create(Employee employee, Long departmentId, Long contractId) {

        Department department = departmentService.viewDepartment(departmentId);
        Contract contract = contractRepository.findById(contractId).orElse(null);

        if(department == null || contract == null){
            return null;
        }
        employee.setDepartment(department);
        employee.setContract(contract);

        return employeeRepository.save(employee);

    }

     @Override
     public EmployeeResponseDTO update(Long id, Employee employee) {
         Employee existingEmployee = employeeRepository.findById(id)
                 .orElseThrow(() ->
                         new RuntimeException("Employee not found: " + id));

         Employee updatedEmployee = employeeRepository.save(existingEmployee);

         return employeeMapper.toResponseDTO(updatedEmployee);
     }

     @Override
     public Employee viewEmployee(Long id) {
         return employeeRepository.findById(id)
                 .orElseThrow(() ->
                         new RuntimeException("Employee not found: " + id));

     }

    @Override
    public List<EmployeeResponseDTO> viewAll() {
        return employeeRepository.findAll()
                .stream()
                .map(employeeMapper::toResponseDTO)
                .toList();
    }

     @Override
     public List<EmployeeResponseDTO> viewAllByManager(Long managerId){
        return employeeRepository.findAll().stream()
                .filter(e -> e.getRole().equals(RoleType.MANAGER))
                .map(employeeMapper::toResponseDTO)
                .toList();
     }

     @Override
     public List<EmployeeResponseDTO> searchForEmployee(String name, Long departmentId, Pageable pageable){
        return null;
     }

}
