package org.example.atlasrh;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.example.atlasrh.Enum.LeaveStatus;
import org.example.atlasrh.Enum.RoleType;
import org.example.atlasrh.model.Contract;
import org.example.atlasrh.model.Department;
import org.example.atlasrh.model.Employee;
import org.example.atlasrh.model.LeaveRequest;
import org.example.atlasrh.model.LeaveType;
import org.example.atlasrh.repository.ContractRepository;
import org.example.atlasrh.repository.DepartmentRepository;
import org.example.atlasrh.repository.EmployeeRepository;
import org.example.atlasrh.repository.LeaveRequestRepository;
import org.example.atlasrh.repository.LeaveTypeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AtlasRhIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private ContractRepository contractRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private LeaveTypeRepository leaveTypeRepository;
    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Test
    void createsEmployeeSubmitsLeaveAndApprovesItThroughAllLayers() throws Exception {
        Department department = departmentRepository.save(new Department("People", "HR"));
        Contract contract = contractRepository.save(new Contract(Contract.ContractType.CDI,
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(30), 60_000L));
        LeaveType leaveType = leaveTypeRepository.save(new LeaveType("Annual", 20L));

        String employeeResponse = mockMvc.perform(post("/api/employee")
                        .queryParam("departmentId", Long.toString(department.getId()))
                        .queryParam("contractId", contract.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"Riley","lastName":"Morgan",
                                 "email":"riley@atlasrh.test","jobTitle":"Manager",
                                 "hireDate":"2021-01-10","role":"MANAGER"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("riley@atlasrh.test"))
                .andReturn().getResponse().getContentAsString();
        JsonNode employeeJson = objectMapper.readTree(employeeResponse);
        long employeeId = employeeJson.get("matricule").asLong();

        String requestResponse = mockMvc.perform(post("/api/leaverequest")
                        .queryParam("employeeId", Long.toString(employeeId))
                        .queryParam("leaveTypeId", leaveType.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"startDate":"2026-12-01","endDate":"2026-12-02",
                                 "reason":"Personal time"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reason").value("Personal time"))
                .andReturn().getResponse().getContentAsString();
        long requestId = objectMapper.readTree(requestResponse).get("id").asLong();

        mockMvc.perform(put("/api/leaverequest/{requestId}/approve", requestId)
                        .queryParam("managerId", Long.toString(employeeId))
                        .queryParam("comment", "Approved"))
                .andExpect(status().isOk())
                .andExpect(content().string("Leave request approved successfully"));

        LeaveRequest approved = leaveRequestRepository.findById(requestId).orElseThrow();
        assertEquals(LeaveStatus.APPROVED, approved.getStatus());
        assertEquals(employeeId, approved.getEmployee().getMatricule());
    }

    @Test
    void rejectsDeletingDepartmentThatContainsEmployees() throws Exception {
        Department department = departmentRepository.save(new Department("Engineering", "Builds systems"));
        Employee employee = new Employee("Casey", "Jones", "casey@atlasrh.test", "Engineer",
                LocalDate.of(2020, 5, 1), RoleType.RH_MANAGER);
        employee.setDepartment(department);
        employeeRepository.save(employee);

        mockMvc.perform(delete("/api/department/{id}", department.getId()))
                .andExpect(status().isConflict())
                .andExpect(content().string("Cannot delete a department that has employees"));

        assertEquals(1, departmentRepository.count());
    }
}
