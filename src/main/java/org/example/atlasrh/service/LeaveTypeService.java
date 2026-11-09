package org.example.atlasrh.service;

import org.example.atlasrh.dto.LeaveTypeResponseDTO;
import org.example.atlasrh.model.LeaveType;

import java.util.List;

public interface LeaveTypeService {
    LeaveTypeResponseDTO create(LeaveType leaveType);

    LeaveType viewLeaveType(Long id);

    List<LeaveTypeResponseDTO> viewAll();


}
