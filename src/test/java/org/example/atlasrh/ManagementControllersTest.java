package org.example.atlasrh;

import org.example.atlasrh.Enum.LeaveStatus;
import org.example.atlasrh.controller.*;
import org.example.atlasrh.dto.*;
import org.example.atlasrh.model.*;
import org.example.atlasrh.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {
        DepartmentController.class, ContractController.class,
        LeaveTypeController.class, LeaveRequestController.class
})
class ManagementControllersTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private DepartmentService departmentService;
    @MockitoBean
    private ContractService contractService;
    @MockitoBean
    private LeaveTypeService leaveTypeService;
    @MockitoBean
    private LeaveRequestService leaveRequestService;

    @Test
    void departmentEndpointsReturnCreatedOkNoContentAndConflict() throws Exception {
        when(departmentService.create(any(Department.class)))
                .thenReturn(new DepartmentResponseDTO(1L, "People", "HR"));
        when(departmentService.viewAll()).thenReturn(List.of(
                new DepartmentResponseDTO(1L, "People", "HR")));

        mockMvc.perform(post("/api/department").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"People\",\"description\":\"HR\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("People"));
        mockMvc.perform(get("/api/department"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
        mockMvc.perform(delete("/api/department/1"))
                .andExpect(status().isNoContent());

        doThrow(new IllegalStateException("Has employees"))
                .when(departmentService).deleteDepartment(2L);
        mockMvc.perform(delete("/api/department/2"))
                .andExpect(status().isConflict());
    }

    @Test
    void departmentCreationAndDeletionReportErrors() throws Exception {
        when(departmentService.create(any(Department.class))).thenThrow(new RuntimeException("Invalid"));
        doThrow(new RuntimeException("Missing")).when(departmentService).deleteDepartment(8L);

        mockMvc.perform(post("/api/department").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"People\",\"description\":\"HR\"}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(delete("/api/department/8"))
                .andExpect(status().isNotFound());
    }

    @Test
    void contractEndpointsReturnDataAndMapExceptions() throws Exception {
        ContractResponseDTO dto = new ContractResponseDTO(5L, "CDI",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), 50_000L);
        when(contractService.viewAll()).thenReturn(List.of(dto));
        when(contractService.create(any(Contract.class), eq(2L))).thenReturn(dto);

        mockMvc.perform(get("/api/contract"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].contractType").value("CDI"));
        mockMvc.perform(post("/api/contract/2").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"contractType":"CDI","startDate":"2026-01-01",
                                 "endDate":"2026-12-31","salary":50000}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5));

        when(contractService.viewAll()).thenThrow(new RuntimeException("Read failed"));
        when(contractService.create(any(Contract.class), eq(2L)))
                .thenThrow(new RuntimeException("Create failed"));
        mockMvc.perform(get("/api/contract")).andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/contract/2").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"contractType":"CDI","startDate":"2026-01-01",
                                 "endDate":"2026-12-31","salary":50000}
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void leaveTypeEndpointsReturnDataAndNotFound() throws Exception {
        when(leaveTypeService.create(any(LeaveType.class)))
                .thenReturn(new LeaveTypeResponseDTO(3, "Annual", 20L));
        when(leaveTypeService.viewLeaveType(3L)).thenReturn(new LeaveType("Annual", 20L));
        when(leaveTypeService.viewAll()).thenReturn(List.of(
                new LeaveTypeResponseDTO(3, "Annual", 20L)));

        mockMvc.perform(post("/api/leavetype").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Annual\",\"annualallowancedays\":20}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Annual"));
        mockMvc.perform(get("/api/leavetype/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Annual"));
        mockMvc.perform(get("/api/leavetype"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].annualallowancedays").value(20));

        when(leaveTypeService.viewLeaveType(99L)).thenThrow(new RuntimeException("Missing"));
        mockMvc.perform(get("/api/leavetype/99")).andExpect(status().isNotFound());
        when(leaveTypeService.create(any(LeaveType.class))).thenThrow(new RuntimeException("Invalid"));
        mockMvc.perform(post("/api/leavetype").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Annual\",\"annualallowancedays\":20}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void leaveRequestReadAndCreateEndpointsReturnJson() throws Exception {
        LeaveRequestResponseDTO dto = leaveRequestDto();
        when(leaveRequestService.create(any(LeaveRequest.class), eq(1L), eq(2L))).thenReturn(dto);
        when(leaveRequestService.viewLeaveRequest(4L)).thenReturn(
                new LeaveRequest(LocalDate.now(), LocalDate.now(), "Holiday", LeaveStatus.PENDING));
        when(leaveRequestService.viewAll()).thenReturn(List.of(dto));
        when(leaveRequestService.getRequests(eq(1L), any())).thenReturn(List.of(dto));
        when(leaveRequestService.getMyRequestsByStatus(eq(1L), eq(LeaveStatus.PENDING), any()))
                .thenReturn(List.of(dto));

        mockMvc.perform(post("/api/leaverequest").queryParam("employeeId", "1")
                        .queryParam("leaveTypeId", "2").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"startDate\":\"2026-12-01\",\"endDate\":\"2026-12-02\",\"reason\":\"Holiday\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reason").value("Holiday"));
        mockMvc.perform(get("/api/leaverequest/4")).andExpect(status().isOk());
        mockMvc.perform(get("/api/leaverequest")).andExpect(status().isOk());
        mockMvc.perform(get("/api/leaverequest/employee/1")).andExpect(status().isOk());
        mockMvc.perform(get("/api/leaverequest/employee/1/status")
                        .queryParam("status", "PENDING"))
                .andExpect(status().isOk());

        when(leaveRequestService.create(any(LeaveRequest.class), eq(1L), eq(2L)))
                .thenThrow(new IllegalArgumentException("Invalid request"));
        mockMvc.perform(post("/api/leaverequest").queryParam("employeeId", "1")
                        .queryParam("leaveTypeId", "2").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"startDate\":\"2026-12-01\",\"endDate\":\"2026-12-02\",\"reason\":\"Holiday\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void leaveRequestUpdateDoesNotBindClientControlledDecisionOrOwnership() throws Exception {
        when(leaveRequestService.update(eq(4L), any())).thenAnswer(invocation -> {
            LeaveRequest update = invocation.getArgument(1);
            assertNull(update.getId());
            assertNull(update.getStatus());
            assertNull(update.getDecisionComment());
            assertNull(update.getEmployee());
            assertNull(update.getLeavetype());
            return leaveRequestDto();
        });

        mockMvc.perform(put("/api/leaverequest/4").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":99,"startDate":"2026-12-01","endDate":"2026-12-02",
                                 "reason":"Holiday","status":"APPROVED","decisionComment":"Injected",
                                 "employee":{"matricule":88},"leavetype":{"id":7}}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reason").value("Holiday"));
    }

    @Test
    void leaveRequestDecisionAndCancellationEndpointsHandleBothOutcomes() throws Exception {
        when(leaveRequestService.cancelRequest(1L, 4L)).thenReturn(true);
        when(leaveRequestService.approveRequest(2L, 4L, "ok")).thenReturn(true);
        when(leaveRequestService.rejectRequest(2L, 5L, "no")).thenReturn(false);
        mockMvc.perform(put("/api/leaverequest/cancel")
                        .queryParam("employeeId", "1").queryParam("requestId", "4"))
                .andExpect(status().isOk());
        mockMvc.perform(put("/api/leaverequest/4/approve")
                        .queryParam("managerId", "2").queryParam("comment", "ok"))
                .andExpect(status().isOk());
        mockMvc.perform(put("/api/leaverequest/5/reject")
                        .queryParam("managerId", "2").queryParam("comment", "no"))
                .andExpect(status().isBadRequest());

        when(leaveRequestService.cancelRequest(1L, 6L)).thenThrow(new RuntimeException("Denied"));
        when(leaveRequestService.approveRequest(2L, 6L, null)).thenThrow(new RuntimeException("Denied"));
        when(leaveRequestService.rejectRequest(2L, 6L, null)).thenThrow(new RuntimeException("Denied"));
        mockMvc.perform(put("/api/leaverequest/cancel")
                        .queryParam("employeeId", "1").queryParam("requestId", "6"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(put("/api/leaverequest/6/approve").queryParam("managerId", "2"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(put("/api/leaverequest/6/reject").queryParam("managerId", "2"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void leaveRequestLookupsAndBalancesReportErrors() throws Exception {
        when(leaveRequestService.viewLeaveRequest(99L)).thenThrow(new RuntimeException("Missing"));
        when(leaveRequestService.viewAll()).thenThrow(new RuntimeException("Unavailable"));
        when(leaveRequestService.getRequests(eq(9L), any())).thenThrow(new RuntimeException("Missing"));
        when(leaveRequestService.getMyRequestsByStatus(eq(9L), eq(LeaveStatus.PENDING), any()))
                .thenThrow(new RuntimeException("Invalid status"));
        when(leaveRequestService.getLeaveBalances(9L)).thenThrow(new IllegalArgumentException("Missing employee"));
        when(leaveRequestService.getLengthOfService(9L)).thenThrow(new IllegalArgumentException("Missing employee"));

        mockMvc.perform(get("/api/leaverequest/99")).andExpect(status().isNotFound());
        mockMvc.perform(get("/api/leaverequest")).andExpect(status().isNotFound());
        mockMvc.perform(get("/api/leaverequest/employee/9")).andExpect(status().isNotFound());
        mockMvc.perform(get("/api/leaverequest/employee/9/status")
                        .queryParam("status", "PENDING"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/leaverequest/employee/9/balance")).andExpect(status().isNotFound());
        mockMvc.perform(get("/api/leaverequest/employee/9/length-of-service"))
                .andExpect(status().isNotFound());
    }

    @Test
    void lengthOfServiceMapsInvalidHireDateToBadRequest() throws Exception {
        when(leaveRequestService.getLengthOfService(1L))
                .thenThrow(new IllegalStateException("Invalid hire date"));

        mockMvc.perform(get("/api/leaverequest/employee/1/length-of-service"))
                .andExpect(status().isBadRequest());
    }

    private LeaveRequestResponseDTO leaveRequestDto() {
        return new LeaveRequestResponseDTO(4L, LocalDate.of(2026, 12, 1),
                LocalDate.of(2026, 12, 2), "Holiday", null);
    }
}
