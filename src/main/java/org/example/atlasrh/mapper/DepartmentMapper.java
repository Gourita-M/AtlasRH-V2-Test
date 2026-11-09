package org.example.atlasrh.mapper;

import org.example.atlasrh.dto.DepartmentResponseDTO;
import org.example.atlasrh.model.Department;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DepartmentMapper{

    DepartmentResponseDTO toResponseDTO(Department department);
}
