package org.example.atlasrh.mapper;

import org.example.atlasrh.dto.LeaveTypeResponseDTO;
import org.example.atlasrh.model.LeaveType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LeaveTypeMapper{

    LeaveTypeResponseDTO toResponseDTO(LeaveType leavetype);
}
