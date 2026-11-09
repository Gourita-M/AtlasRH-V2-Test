package org.example.atlasrh.mapper;

import org.example.atlasrh.dto.EmployeeResponseDTO;
import org.example.atlasrh.model.Employee;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmployeeMapper{

    EmployeeResponseDTO toResponseDTO(Employee employee);

}