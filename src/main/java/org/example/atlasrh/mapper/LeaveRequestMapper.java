package org.example.atlasrh.mapper;

import org.example.atlasrh.dto.LeaveRequestResponseDTO;
import org.example.atlasrh.model.LeaveRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LeaveRequestMapper{

    @Mapping(target = "id", expression = "java(leaveRequest.getId())")
    @Mapping(target = "startDate", expression = "java(leaveRequest.getStartDate())")
    @Mapping(target = "endDate", expression = "java(leaveRequest.getEndDate())")
    @Mapping(target = "reason", expression = "java(leaveRequest.getReason())")
    @Mapping(target = "decisionComment", expression = "java(leaveRequest.getDecisionComment())")
    LeaveRequestResponseDTO toResponseDTO(LeaveRequest leaveRequest);
}
