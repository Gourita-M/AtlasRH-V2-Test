package org.example.atlasrh.service;

import org.example.atlasrh.Enum.LeaveStatus;
import org.example.atlasrh.Enum.RoleType;
import org.example.atlasrh.dto.LeaveBalanceResponseDTO;
import org.example.atlasrh.dto.LengthOfServiceResponseDTO;
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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.Year;

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
        if (leaveRequest == null || leaveRequest.getStartDate() == null
                || leaveRequest.getEndDate() == null
                || leaveRequest.getStartDate().isAfter(leaveRequest.getEndDate())) {
            throw new IllegalArgumentException("Start Date is After End Date");
        }

        LeaveType leavetype = leaveTypeId == null ? null
                : leaveTypeRepository.findById(Math.toIntExact(leaveTypeId)).orElse(null);
        Employee employee = employeeId == null ? null : employeeRepository.findById(employeeId).orElse(null);
        if (leavetype == null || employee == null) {
            throw new IllegalArgumentException("Leave Type or Employee is Null");
        }
        leaveRequest.setEmployee(employee);
        leaveRequest.setLeavetype(leavetype);
        if (leaveRequest.getStatus() == null) {
            leaveRequest.setStatus(LeaveStatus.PENDING);
        }
        LeaveRequest saved = leaveRequestRepository.save(leaveRequest);
        return leaveRequestMapper.toResponseDTO(saved);
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
    public LeaveRequest viewLeaveRequest(Long id){
        LeaveRequest leaveRequest = leaveRequestRepository
                .findById(id).orElseThrow(() -> new RuntimeException("Leave Request not found"));

        return leaveRequest;
        //return leaveRequestMapper.toResponseDTO(leaveRequest);
    }

    @Override
    public List<LeaveRequestResponseDTO> viewAll(){
        return leaveRequestRepository.findAll()
                .stream().map(leaveRequestMapper::toResponseDTO).toList();
    }

    @Override
    public List<LeaveRequestResponseDTO> getRequests(Long employeeId, Pageable pageable) {
        return leaveRequestRepository.findAllByEmployee_Matricule(employeeId, pageable)
                .map(leaveRequestMapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<LeaveRequestResponseDTO> getMyRequestsByStatus(Long employeeId, LeaveStatus status, Pageable pageable){
        return leaveRequestRepository.findAllByEmployee_MatriculeAndStatus(employeeId, status, pageable)
                .map(leaveRequestMapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<LeaveBalanceResponseDTO> getLeaveBalances(Long employeeId) {
        requireEmployee(employeeId);
        Year currentYear = Year.now();
        LocalDate yearStart = currentYear.atDay(1);
        LocalDate yearEnd = currentYear.atDay(currentYear.length());
        List<LeaveRequest> approvedRequests = leaveRequestRepository
                .findAllByEmployee_MatriculeAndStatus(employeeId, LeaveStatus.APPROVED);

        return leaveTypeRepository.findAll().stream()
                .map(leaveType -> {
                    long approvedDaysUsed = approvedRequests.stream()
                            .filter(request -> request.getLeavetype() != null
                                    && request.getLeavetype().getId().equals(leaveType.getId()))
                            .mapToLong(request -> countWorkingDaysInPeriod(
                                    request.getStartDate(), request.getEndDate(), yearStart, yearEnd))
                            .sum();
                    return new LeaveBalanceResponseDTO(
                            leaveType.getId(),
                            leaveType.getName(),
                            leaveType.getAnnualallowancedays(),
                            approvedDaysUsed,
                            leaveType.getAnnualallowancedays() - approvedDaysUsed
                    );
                })
                .toList();
    }

    @Override
    public LengthOfServiceResponseDTO getLengthOfService(Long employeeId) {
        Employee employee = requireEmployee(employeeId);
        LocalDate today = LocalDate.now();
        if (employee.getHireDate() == null || employee.getHireDate().isAfter(today)) {
            throw new IllegalStateException("Employee has an invalid hire date: " + employeeId);
        }

        Period servicePeriod = Period.between(employee.getHireDate(), today);
        return new LengthOfServiceResponseDTO(servicePeriod.getYears(), servicePeriod.getMonths());
    }

    private Employee requireEmployee(Long employeeId) {
        if (employeeId == null) {
            throw new IllegalArgumentException("Employee ID is required");
        }
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + employeeId));
    }

    private long countWorkingDaysInPeriod(LocalDate startDate, LocalDate endDate,
                                          LocalDate periodStart, LocalDate periodEnd) {
        LocalDate start = startDate.isAfter(periodStart) ? startDate : periodStart;
        LocalDate end = endDate.isBefore(periodEnd) ? endDate : periodEnd;
        if (start.isAfter(end)) {
            return 0;
        }

        long workingDays = 0;
        for (LocalDate day = start; !day.isAfter(end); day = day.plusDays(1)) {
            DayOfWeek dayOfWeek = day.getDayOfWeek();
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                workingDays++;
            }
        }
        return workingDays;
    }

    @Override
    public boolean cancelRequest(Long employeeId, Long requestId) {
        LeaveRequest leaveRequest = leaveRequestRepository.findByIdAndEmployee_Matricule(requestId, employeeId);
        if (leaveRequest != null && leaveRequest.getStatus() == LeaveStatus.PENDING) {
            leaveRequest.setStatus(LeaveStatus.CANCELLED);
            leaveRequestRepository.save(leaveRequest);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean approveRequest(Long managerId, Long requestId, String comment){
        if (requestId == null || !isManager(managerId)) {
            return false;
        }

        LeaveRequest leaveRequest = leaveRequestRepository.findById(requestId).orElse(null);
        if (leaveRequest == null || leaveRequest.getStatus() != LeaveStatus.PENDING) {
            return false;
        }

        leaveRequest.setStatus(LeaveStatus.APPROVED);
        leaveRequest.setDecisionComment(normalizeComment(comment));
        leaveRequestRepository.save(leaveRequest);
        return true;
    }

    @Override
    public boolean rejectRequest(Long managerId, Long requestId, String comment){
        String decisionComment = normalizeComment(comment);
        if (requestId == null || !isManager(managerId) || decisionComment == null) {
            return false;
        }

        LeaveRequest leaveRequest = leaveRequestRepository.findById(requestId).orElse(null);
        if (leaveRequest == null || leaveRequest.getStatus() != LeaveStatus.PENDING) {
            return false;
        }

        leaveRequest.setStatus(LeaveStatus.REJECTED);
        leaveRequest.setDecisionComment(decisionComment);
        leaveRequestRepository.save(leaveRequest);
        return true;
    }

    private boolean isManager(Long managerId) {
        return managerId != null
                && employeeRepository.findById(managerId)
                .map(manager -> manager.getRole() == RoleType.MANAGER)
                .orElse(false);
    }

    private String normalizeComment(String comment) {
        if (comment == null || comment.isBlank()) {
            return null;
        }
        return comment.trim();
    }

}
