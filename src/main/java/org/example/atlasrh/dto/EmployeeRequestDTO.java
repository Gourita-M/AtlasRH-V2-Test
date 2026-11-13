package org.example.atlasrh.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.atlasrh.Enum.RoleType;
import org.example.atlasrh.model.Employee;

import java.time.LocalDate;

public record EmployeeRequestDTO(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Email String email,
        @NotBlank String jobTitle,
        @NotNull LocalDate hireDate,
        @NotNull RoleType role,
        Long managerId
) {
    public Employee toEmployee() {
        Employee employee = new Employee(firstName, lastName, email, jobTitle, hireDate, role);
        if (managerId != null) {
            Employee manager = new Employee();
            manager.setMatricule(managerId);
            employee.setManager(manager);
        }
        return employee;
    }
}
