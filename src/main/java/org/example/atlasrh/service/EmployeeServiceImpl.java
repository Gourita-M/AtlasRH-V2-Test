package org.example.atlasrh.service;

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
        if (employee == null) {
            throw new IllegalArgumentException("Employee is required");
        }
        if (employee.getMatricule() != 0 && employeeRepository.existsById(employee.getMatricule())) {
            throw new IllegalArgumentException("Employee number already exists");
        }
        if (employeeRepository.existsByEmail(employee.getEmail())) {
            throw new IllegalArgumentException("Employee email already exists");
        }

        validateAndResolveManager(employee, null);

        Department department = departmentService.viewDepartment(departmentId);
        Contract contract = contractRepository.findById(contractId).orElse(null);

        if (department == null || contract == null) {
            throw new IllegalArgumentException("Department or contract was not found");
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

         if (employeeRepository.existsByEmailAndMatriculeNot(employee.getEmail(), id)) {
             throw new IllegalArgumentException("Employee email already exists");
         }
         validateAndResolveManager(employee, id);
         existingEmployee.setFirstName(employee.getFirstName());
         existingEmployee.setLastName(employee.getLastName());
         existingEmployee.setEmail(employee.getEmail());
         existingEmployee.setJobTitle(employee.getJobTitle());
         existingEmployee.setHireDate(employee.getHireDate());
         existingEmployee.setRole(employee.getRole());
         existingEmployee.setManager(employee.getManager());

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
        return employeeRepository.findAllByManager_Matricule(managerId).stream()
                .map(employeeMapper::toResponseDTO)
                .toList();
     }

     @Override
     public List<EmployeeResponseDTO> searchForEmployee(String name, Long departmentId, Pageable pageable){
        return employeeRepository.searchEmployees(name == null ? "" : name.trim(),
                        departmentId, pageable)
                .map(employeeMapper::toResponseDTO)
                .toList();
     }

     @Override
     public List<EmployeeResponseDTO> getEmplyeeByMart(Long matr){
        return employeeRepository.findById(matr)
                .map(employeeMapper::toResponseDTO)
                .stream()
                .toList();
     }

     private void validateAndResolveManager(Employee employee, Long employeeId) {
         Employee manager = employee.getManager();
         if (manager == null) {
             return;
         }
         if (manager == employee
                 || (employeeId != null && manager.getMatricule() == employeeId)
                 || (employeeId == null && employee.getMatricule() != 0
                     && manager.getMatricule() == employee.getMatricule())) {
             throw new IllegalArgumentException("An employee cannot be their own manager");
         }
         if (manager.getMatricule() == 0) {
             throw new IllegalArgumentException("A manager must have an employee number");
         }
         employee.setManager(employeeRepository.findById(manager.getMatricule())
                 .orElseThrow(() -> new IllegalArgumentException("Manager was not found")));
     }
}
