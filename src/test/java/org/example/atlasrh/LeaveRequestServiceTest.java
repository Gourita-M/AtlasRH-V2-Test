package org.example.atlasrh;

import org.example.atlasrh.Enum.LeaveStatus;
import org.example.atlasrh.Enum.RoleType;
import org.example.atlasrh.dto.LeaveRequestResponseDTO;
import org.example.atlasrh.mapper.LeaveRequestMapper;
import org.example.atlasrh.model.Employee;
import org.example.atlasrh.model.LeaveRequest;
import org.example.atlasrh.model.LeaveType;
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
import java.util.List;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

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

    @Test
    void cancelsPendingRequestOwnedByEmployee() {
        LeaveRequest request = new LeaveRequest();
        request.setStatus(LeaveStatus.PENDING);
        when(leaveRequestRepository.findByIdAndEmployee_Matricule(2L, 1L)).thenReturn(request);

        assertTrue(leaveRequestService.cancelRequest(1L, 2L));
        assertEquals(LeaveStatus.CANCELLED, request.getStatus());
        verify(leaveRequestRepository).save(request);
    }

    @Test
    void doesNotCancelRequestOwnedByAnotherEmployee() {
        when(leaveRequestRepository.findByIdAndEmployee_Matricule(2L, 1L)).thenReturn(null);

        assertFalse(leaveRequestService.cancelRequest(1L, 2L));
        verify(leaveRequestRepository, never()).save(any());
    }

    @Test
    void createsPendingRequestForExistingEmployeeAndLeaveType() {
        Employee employee = new Employee();
        LeaveType leaveType = new LeaveType("Annual", 20L);
        LeaveRequest request = new LeaveRequest(LocalDate.now(), LocalDate.now().plusDays(1),
                "Holiday", null);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(leaveTypeRepository.findById(3)).thenReturn(Optional.of(leaveType));
        when(leaveRequestRepository.save(request)).thenReturn(request);
        when(leaveRequestMapper.toResponseDTO(request)).thenReturn(
                new LeaveRequestResponseDTO(null, request.getStartDate(), request.getEndDate(),
                        request.getReason(), null));

        assertEquals("Holiday", leaveRequestService.create(request, 1L, 3L).reason());
        assertEquals(LeaveStatus.PENDING, request.getStatus());
        assertEquals(employee, request.getEmployee());
        assertEquals(leaveType, request.getLeavetype());
        verify(leaveRequestRepository).save(request);
    }

    @Test
    void rejectsLeaveRequestWithEndDateBeforeStartDate() {
        LeaveRequest request = new LeaveRequest(LocalDate.now().plusDays(1), LocalDate.now(),
                "Invalid dates", LeaveStatus.PENDING);

        assertThrows(IllegalArgumentException.class,
                () -> leaveRequestService.create(request, 1L, 3L));
        verifyNoInteractions(leaveTypeRepository, employeeRepository);
    }

    @Test
    void doesNotCancelAlreadyDecidedRequest() {
        LeaveRequest request = new LeaveRequest();
        request.setStatus(LeaveStatus.APPROVED);
        when(leaveRequestRepository.findByIdAndEmployee_Matricule(2L, 1L)).thenReturn(request);

        assertFalse(leaveRequestService.cancelRequest(1L, 2L));
        verify(leaveRequestRepository, never()).save(any());
    }

    @Test
    void updatesViewsAndListsRequestsByEmployeeAndStatus() {
        LeaveRequest request = new LeaveRequest(LocalDate.now(), LocalDate.now(),
                "Holiday", LeaveStatus.PENDING);
        LeaveRequestResponseDTO dto = new LeaveRequestResponseDTO(2L, request.getStartDate(),
                request.getEndDate(), request.getReason(), null);
        PageRequest pageable = PageRequest.of(0, 10);
        when(leaveRequestRepository.findById(2L)).thenReturn(Optional.of(request));
        when(leaveRequestRepository.findById(3L)).thenReturn(Optional.empty());
        when(leaveRequestRepository.save(request)).thenReturn(request);
        when(leaveRequestRepository.findAll()).thenReturn(List.of(request));
        when(leaveRequestRepository.findAllByEmployee_Matricule(1L, pageable))
                .thenReturn(new PageImpl<>(List.of(request)));
        when(leaveRequestRepository.findAllByEmployee_MatriculeAndStatus(
                1L, LeaveStatus.PENDING, pageable)).thenReturn(new PageImpl<>(List.of(request)));
        when(leaveRequestMapper.toResponseDTO(request)).thenReturn(dto);

        assertEquals(dto, leaveRequestService.update(2L, request));
        assertNull(leaveRequestService.update(3L, request));
        assertSame(request, leaveRequestService.viewLeaveRequest(2L));
        assertThrows(RuntimeException.class, () -> leaveRequestService.viewLeaveRequest(3L));
        assertEquals(List.of(dto), leaveRequestService.viewAll());
        assertEquals(List.of(dto), leaveRequestService.getRequests(1L, pageable));
        assertEquals(List.of(dto),
                leaveRequestService.getMyRequestsByStatus(1L, LeaveStatus.PENDING, pageable));
    }

    @Test
    void rejectsLengthOfServiceForMissingOrInvalidHireDate() {
        Employee employee = new Employee();
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(employee));
        when(employeeRepository.findById(3L)).thenReturn(Optional.empty());
        employee.setHireDate(LocalDate.now().plusDays(1));

        assertThrows(IllegalStateException.class, () -> leaveRequestService.getLengthOfService(1L));
        employee.setHireDate(null);
        assertThrows(IllegalStateException.class, () -> leaveRequestService.getLengthOfService(2L));
        assertThrows(IllegalArgumentException.class, () -> leaveRequestService.getLengthOfService(3L));
    }
}
