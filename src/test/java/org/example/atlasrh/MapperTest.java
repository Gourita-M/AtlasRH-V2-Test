package org.example.atlasrh;

import org.example.atlasrh.Enum.LeaveStatus;
import org.example.atlasrh.Enum.RoleType;
import org.example.atlasrh.mapper.*;
import org.example.atlasrh.model.*;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MapperTest {
    private final EmployeeMapper employeeMapper = Mappers.getMapper(EmployeeMapper.class);
    private final DepartmentMapper departmentMapper = Mappers.getMapper(DepartmentMapper.class);
    private final ContractMapper contractMapper = Mappers.getMapper(ContractMapper.class);
    private final LeaveTypeMapper leaveTypeMapper = Mappers.getMapper(LeaveTypeMapper.class);
    private final LeaveRequestMapper leaveRequestMapper = Mappers.getMapper(LeaveRequestMapper.class);

    @Test
    void mapsEmployeeFieldsAndHandlesNull() {
        Employee employee = new Employee("Ada", "Lovelace", "ada@atlasrh.test", "Analyst",
                LocalDate.of(2020, 1, 1), RoleType.MANAGER);
        employee.setMatricule(1L);

        var dto = employeeMapper.toResponseDTO(employee);

        assertEquals(1L, dto.matricule());
        assertEquals("Ada", dto.firstName());
        assertEquals("MANAGER", dto.role());
        assertNull(employeeMapper.toResponseDTO(null));
    }

    @Test
    void mapsDepartmentFields() {
        Department department = new Department("People", "HR");
        department.setId(2L);

        var dto = departmentMapper.toResponseDTO(department);

        assertEquals(2L, dto.id());
        assertEquals("People", dto.name());
        assertEquals("HR", dto.description());
    }

    @Test
    void mapsContractFields() {
        Contract contract = new Contract(Contract.ContractType.CDI, LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31), 50_000L);
        contract.setId(3L);

        var dto = contractMapper.toResponseDTO(contract);

        assertEquals(3L, dto.id());
        assertEquals("CDI", dto.contractType());
        assertEquals(50_000L, dto.salary());
    }

    @Test
    void mapsLeaveTypeFields() {
        LeaveType leaveType = new LeaveType("Annual", 20L);
        leaveType.setId(4);

        var dto = leaveTypeMapper.toResponseDTO(leaveType);

        assertEquals(4, dto.id());
        assertEquals("Annual", dto.name());
        assertEquals(20L, dto.annualallowancedays());
    }

    @Test
    void mapsLeaveRequestFieldsAndHandlesNull() {
        LeaveRequest leaveRequest = new LeaveRequest(LocalDate.of(2026, 12, 1),
                LocalDate.of(2026, 12, 2), "Holiday", LeaveStatus.PENDING);
        leaveRequest.setId(5L);
        leaveRequest.setDecisionComment("Approved");

        var dto = leaveRequestMapper.toResponseDTO(leaveRequest);

        assertEquals(5L, dto.id());
        assertEquals("Holiday", dto.reason());
        assertEquals("Approved", dto.decisionComment());
        assertNull(leaveRequestMapper.toResponseDTO(null));
    }
}
