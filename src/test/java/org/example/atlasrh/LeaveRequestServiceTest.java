package org.example.atlasrh;

import org.example.atlasrh.Enum.LeaveStatus;
import org.example.atlasrh.Enum.RoleType;
import org.example.atlasrh.mapper.LeaveRequestMapper;
import org.example.atlasrh.model.Employee;
import org.example.atlasrh.model.LeaveRequest;
import org.example.atlasrh.repository.EmployeeRepository;
import org.example.atlasrh.repository.LeaveRequestRepository;
import org.example.atlasrh.repository.LeaveTypeRepository;
import org.example.atlasrh.service.LeaveRequestServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeaveRequestServiceTest {

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private LeaveTypeRepository leaveTypeRepository;

    @Mock
    private LeaveRequestMapper leaveRequestMapper;

    @InjectMocks
    private LeaveRequestServiceImpl leaveRequestService;

    @Test
    void approvesPendingRequestForManager() {
        Long managerId = 1L;
        Long requestId = 2L;
        Employee manager = new Employee();
        manager.setRole(RoleType.MANAGER);
        LeaveRequest request = new LeaveRequest();
        request.setStatus(LeaveStatus.PENDING);

        when(employeeRepository.findById(managerId)).thenReturn(Optional.of(manager));
        when(leaveRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        assertTrue(leaveRequestService.approveRequest(managerId, requestId, "Approved"));
        assertEquals(LeaveStatus.APPROVED, request.getStatus());
        assertEquals("Approved", request.getDecisionComment());
        verify(leaveRequestRepository).save(request);
    }

    @Test
    void rejectsPendingRequestWhenCommentIsProvided() {
        Long managerId = 1L;
        Long requestId = 2L;
        Employee manager = new Employee();
        manager.setRole(RoleType.MANAGER);
        LeaveRequest request = new LeaveRequest();
        request.setStatus(LeaveStatus.PENDING);

        when(employeeRepository.findById(managerId)).thenReturn(Optional.of(manager));
        when(leaveRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        assertTrue(leaveRequestService.rejectRequest(managerId, requestId, "  Missing details  "));
        assertEquals(LeaveStatus.REJECTED, request.getStatus());
        assertEquals("Missing details", request.getDecisionComment());
        verify(leaveRequestRepository).save(request);
    }

    @Test
    void doesNotRejectWithoutComment() {
        Long managerId = 1L;
        Employee manager = new Employee();
        manager.setRole(RoleType.MANAGER);
        when(employeeRepository.findById(managerId)).thenReturn(Optional.of(manager));

        assertFalse(leaveRequestService.rejectRequest(managerId, 2L, "  "));
    }

    @Test
    void doesNotApproveRequestAlreadyDecided() {
        Long managerId = 1L;
        Long requestId = 2L;
        Employee manager = new Employee();
        manager.setRole(RoleType.MANAGER);
        LeaveRequest request = new LeaveRequest();
        request.setStatus(LeaveStatus.REJECTED);

        when(employeeRepository.findById(managerId)).thenReturn(Optional.of(manager));
        when(leaveRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        assertFalse(leaveRequestService.approveRequest(managerId, requestId, null));
    }

    @Test
    void doesNotAllowNonManagerToDecideRequest() {
        Long employeeId = 1L;
        Employee employee = new Employee();
        employee.setRole(RoleType.RH_MANAGER);
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        assertFalse(leaveRequestService.approveRequest(employeeId, 2L, null));
    }
}
