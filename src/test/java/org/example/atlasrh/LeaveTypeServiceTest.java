package org.example.atlasrh;

import org.example.atlasrh.dto.LeaveTypeResponseDTO;
import org.example.atlasrh.mapper.LeaveTypeMapper;
import org.example.atlasrh.model.LeaveType;
import org.example.atlasrh.repository.LeaveTypeRepository;
import org.example.atlasrh.service.LeaveTypeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveTypeServiceTest {
    @Mock
    private LeaveTypeRepository leaveTypeRepository;
    @Mock
    private LeaveTypeMapper leaveTypeMapper;
    @InjectMocks
    private LeaveTypeServiceImpl leaveTypeService;

    @Test
    void createsAndListsLeaveTypes() {
        LeaveType leaveType = new LeaveType("Annual", 20L);
        LeaveTypeResponseDTO response = new LeaveTypeResponseDTO(1, "Annual", 20L);
        when(leaveTypeRepository.save(leaveType)).thenReturn(leaveType);
        when(leaveTypeRepository.findAll()).thenReturn(List.of(leaveType));
        when(leaveTypeMapper.toResponseDTO(leaveType)).thenReturn(response);

        assertEquals(response, leaveTypeService.create(leaveType));
        assertEquals(List.of(response), leaveTypeService.viewAll());
    }

    @Test
    void findsLeaveTypeAndReportsMissingId() {
        LeaveType leaveType = new LeaveType("Annual", 20L);
        when(leaveTypeRepository.findById(1)).thenReturn(Optional.of(leaveType));
        when(leaveTypeRepository.findById(2)).thenReturn(Optional.empty());

        assertSame(leaveType, leaveTypeService.viewLeaveType(1L));
        assertThrows(RuntimeException.class, () -> leaveTypeService.viewLeaveType(2L));
    }
}
