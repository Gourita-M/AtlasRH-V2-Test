package org.example.atlasrh.service;

import org.example.atlasrh.dto.LeaveTypeResponseDTO;
import org.example.atlasrh.mapper.LeaveTypeMapper;
import org.example.atlasrh.model.LeaveType;
import org.example.atlasrh.repository.LeaveTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaveTypeServiceImpl implements LeaveTypeService {
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveTypeMapper leaveTypeMapper;

    public LeaveTypeServiceImpl(LeaveTypeRepository leaveTypeRepository, LeaveTypeMapper leaveTypeMapper) {
        this.leaveTypeRepository = leaveTypeRepository;
        this.leaveTypeMapper = leaveTypeMapper;
    }

    @Override
    public LeaveTypeResponseDTO create(LeaveType leaveType){
        LeaveType created = leaveTypeRepository.save(leaveType);
        return leaveTypeMapper.toResponseDTO(created);
    }

    @Override
    public LeaveType viewLeaveType(Long id){
        return leaveTypeRepository.findById(id).orElseThrow(() -> new RuntimeException("LeaveType Can't be Found"));
    }

    @Override
    public List<LeaveTypeResponseDTO> viewAll(){
        return leaveTypeRepository.findAll().stream()
                .map(leaveTypeMapper::toResponseDTO)
                .toList();
    }
}
