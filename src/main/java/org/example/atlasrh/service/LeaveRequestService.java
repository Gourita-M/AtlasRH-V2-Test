package org.example.atlasrh.service;

import org.example.atlasrh.Enum.LeaveStatus;
import org.example.atlasrh.dto.LeaveRequestResponseDTO;
import org.example.atlasrh.model.LeaveRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LeaveRequestService {
    LeaveRequestResponseDTO create(LeaveRequest leaveRequest, Long employeeId, Long leaveTypeId);

    LeaveRequestResponseDTO update(Long id, LeaveRequest leaveRequest);

    LeaveRequestResponseDTO viewLeaveRequest(Long id);

    List<LeaveRequestResponseDTO> viewAll();

    List<LeaveRequestResponseDTO> getRequests(Long employeeId, Pageable pageable);

    List<LeaveRequestResponseDTO> getMyRequestsByStatus(Long employeeId, LeaveStatus status, Pageable pageable);

    boolean cancelRequest(Long employeeId, Long requestId);

    boolean approveRequest(Long managerId, Long requestId, String comment);

    boolean rejectRequest(Long managerId, Long requestId, String comment);
}
