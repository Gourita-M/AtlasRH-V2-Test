package org.example.atlasrh.mapper;

import org.example.atlasrh.dto.LeaveRequestResponseDTO;
import org.example.atlasrh.model.LeaveRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LeaveRequestMapper{

    LeaveRequestResponseDTO toResponseDTO(LeaveRequest leaveRequest);
}
