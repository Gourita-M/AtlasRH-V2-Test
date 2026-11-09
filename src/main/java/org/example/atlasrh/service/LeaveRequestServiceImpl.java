package org.example.atlasrh.service;

import org.example.atlasrh.Enum.LeaveStatus;
import org.example.atlasrh.dto.LeaveRequestResponseDTO;
import org.example.atlasrh.mapper.LeaveRequestMapper;
import org.example.atlasrh.model.Employee;
import org.example.atlasrh.model.LeaveRequest;
import org.example.atlasrh.model.LeaveType;
import org.example.atlasrh.repository.EmployeeRepository;
import org.example.atlasrh.repository.LeaveRequestRepository;
import org.example.atlasrh.repository.LeaveTypeRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LeaveRequestServiceImpl implements LeaveRequestService{
    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveRequestMapper leaveRequestMapper;

    public LeaveRequestServiceImpl(LeaveRequestRepository leaveRequestRepository, EmployeeRepository employeeRepository,
                                   LeaveTypeRepository leaveTypeRepository, LeaveRequestMapper leaveRequestMapper) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.employeeRepository = employeeRepository;
        this.leaveTypeRepository = leaveTypeRepository;
        this.leaveRequestMapper = leaveRequestMapper;
    }

    @Override
    public LeaveRequestResponseDTO create(LeaveRequest leaveRequest, Long employeeId, Long leaveTypeId){
        LeaveType leavetype = leaveTypeRepository.findById(leaveTypeId).orElse(null);
        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        if(leavetype == null || employee == null){
            leaveRequest.setEmployee(employee);
            leaveRequest.setLeavetype(leavetype);
            leaveRequestRepository.save(leaveRequest);
            return leaveRequestMapper.toResponseDTO(leaveRequest);
        }else {
            return null;
        }
    }

    @Override
    public LeaveRequestResponseDTO update(Long id, LeaveRequest leaveRequest){
        Optional<LeaveRequest> leaveReq = leaveRequestRepository.findById(id);
        if(leaveReq.isPresent()){
            leaveRequestRepository.save(leaveRequest);
            return leaveRequestMapper.toResponseDTO(leaveRequest);
        }else{
            return null;
        }
    }

    @Override
    public LeaveRequestResponseDTO viewLeaveRequest(Long id){
        LeaveRequest leaveRequest = leaveRequestRepository
                .findById(id).orElseThrow(() -> new RuntimeException("Leave Request not found"));

        return leaveRequestMapper.toResponseDTO(leaveRequest);
    }

    @Override
    public List<LeaveRequestResponseDTO> viewAll(){
        return leaveRequestRepository.findAll()
                .stream().map(leaveRequestMapper::toResponseDTO).toList();
    }

    @Override
    public List<LeaveRequestResponseDTO> getRequests(Long employeeId, Pageable pageable) {
        return null;
    }

    @Override
    public List<LeaveRequestResponseDTO> getMyRequestsByStatus(Long employeeId, LeaveStatus status, Pageable pageable){
        return leaveRequestRepository.findAll().stream()
                .filter(l -> l.getId().equals(employeeId))
                .filter(l -> l.getStatus() == status)
                .map(leaveRequestMapper::toResponseDTO)
                .toList();
    }

    @Override
    public boolean cancelRequest(Long employeeId, Long requestId) {
        LeaveRequest leaveRequest = leaveRequestRepository.findByIdAndEmployee_Matricule(requestId, employeeId);
        if(leaveRequest != null){
            leaveRequest.setStatus(LeaveStatus.CANCELLED);
            leaveRequestRepository.save(leaveRequest);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean approveRequest(Long managerId, Long requestId, String comment){
        return false;
    }

    @Override
    public boolean rejectRequest(Long managerId, Long requestId, String comment){
        return false;
    }

}
