package org.example.atlasrh.dto;

import jakarta.validation.constraints.NotBlank;
import org.example.atlasrh.model.Department;

public record DepartmentRequestDTO(
        @NotBlank String name,
        @NotBlank String description
) {
    public Department toDepartment() {
        return new Department(name, description);
    }
}
