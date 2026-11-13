package org.example.atlasrh;

import org.example.atlasrh.Enum.RoleType;
import org.example.atlasrh.dto.EmployeeResponseDTO;
import org.example.atlasrh.controller.EmployeeController;
import org.example.atlasrh.model.Employee;
import org.example.atlasrh.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private EmployeeService employeeService;

    @Test
    void returnsEmployeeListAsJson() throws Exception {
        when(employeeService.viewAll()).thenReturn(List.of(
                new EmployeeResponseDTO(1L, "Ada", "Lovelace", "ada@atlasrh.test",
                        "Analyst", LocalDate.of(2020, 1, 1), "RH_MANAGER")));

        mockMvc.perform(get("/api/employee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].matricule").value(1))
                .andExpect(jsonPath("$[0].email").value("ada@atlasrh.test"));
    }

    @Test
    void createsValidEmployeeAndReturnsCreatedJson() throws Exception {
        Employee created = employee();
        created.setMatricule(1L);
        when(employeeService.create(any(Employee.class), eq(2L), eq(3L))).thenReturn(created);

        mockMvc.perform(post("/api/employee")
                        .queryParam("departmentId", "2")
                        .queryParam("contractId", "3")
                        .contentType("application/json")
                        .content(employeeJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.matricule").value(1))
                .andExpect(jsonPath("$.email").value("ada@atlasrh.test"));
    }

    @Test
    void rejectsInvalidEmployeePayload() throws Exception {
        mockMvc.perform(post("/api/employee")
                        .queryParam("departmentId", "2")
                        .queryParam("contractId", "3")
                        .contentType("application/json")
                        .content("{\"firstName\":\"\",\"email\":\"not-an-email\"}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(employeeService);
    }

    @Test
    void returnsConflictForDuplicateEmployee() throws Exception {
        when(employeeService.create(any(Employee.class), eq(2L), eq(3L)))
                .thenThrow(new IllegalArgumentException("Employee email already exists"));

        mockMvc.perform(post("/api/employee")
                        .queryParam("departmentId", "2")
                        .queryParam("contractId", "3")
                        .contentType("application/json")
                        .content(employeeJson()))
                .andExpect(status().isConflict())
                .andExpect(content().string("Employee email already exists"));
    }

    @Test
    void returnsNotFoundForUnknownEmployee() throws Exception {
        when(employeeService.viewEmployee(99L)).thenThrow(new RuntimeException("Employee not found: 99"));

        mockMvc.perform(get("/api/employee/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Employee not found: 99"));
    }

    private Employee employee() {
        return new Employee("Ada", "Lovelace", "ada@atlasrh.test", "Analyst",
                LocalDate.of(2020, 1, 1), RoleType.RH_MANAGER);
    }

    private String employeeJson() {
        return """
                {"firstName":"Ada","lastName":"Lovelace","email":"ada@atlasrh.test",
                 "jobTitle":"Analyst","hireDate":"2020-01-01","role":"RH_MANAGER"}
                """;
    }
}
