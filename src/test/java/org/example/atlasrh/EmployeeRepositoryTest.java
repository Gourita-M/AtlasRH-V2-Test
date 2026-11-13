package org.example.atlasrh;

import org.example.atlasrh.Enum.RoleType;
import org.example.atlasrh.model.Department;
import org.example.atlasrh.model.Employee;
import org.example.atlasrh.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
class EmployeeRepositoryTest {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
    void searchesByNameAndDepartmentWithPaginationAndSorting() {
        Department targetDepartment = new Department("Engineering", "Builds systems");
        Department otherDepartment = new Department("Finance", "Manages budgets");
        entityManager.persist(targetDepartment);
        entityManager.persist(otherDepartment);
        entityManager.persist(employee("Sam", "Alpha", "sam.alpha@atlasrh.test", targetDepartment));
        entityManager.persist(employee("Sam", "Zulu", "sam.zulu@atlasrh.test", targetDepartment));
        entityManager.persist(employee("Sam", "Other", "sam.other@atlasrh.test", otherDepartment));
        entityManager.persist(employee("Alex", "Unrelated", "alex@atlasrh.test", targetDepartment));
        entityManager.flush();

        Page<Employee> firstPage = employeeRepository.searchEmployees(
                "sam", targetDepartment.getId(),
                PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "lastName")));
        Page<Employee> secondPage = employeeRepository.searchEmployees(
                "sam", targetDepartment.getId(),
                PageRequest.of(1, 1, Sort.by(Sort.Direction.DESC, "lastName")));

        assertEquals(2, firstPage.getTotalElements());
        assertEquals(2, firstPage.getTotalPages());
        assertEquals("Zulu", firstPage.getContent().get(0).getLastName());
        assertEquals("Alpha", secondPage.getContent().get(0).getLastName());
    }

    @Test
    void filtersByDepartmentWhenNameIsBlank() {
        Department department = new Department("HR", "People");
        entityManager.persist(department);
        entityManager.persist(employee("Morgan", "Lee", "morgan@atlasrh.test", department));
        entityManager.flush();

        Page<Employee> result = employeeRepository.searchEmployees(
                "", department.getId(), PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals("Morgan", result.getContent().get(0).getFirstName());
    }

    private Employee employee(String firstName, String lastName, String email, Department department) {
        Employee employee = new Employee(firstName, lastName, email, "Analyst",
                LocalDate.of(2022, 1, 1), RoleType.RH_MANAGER);
        employee.setDepartment(department);
        return employee;
    }
}
