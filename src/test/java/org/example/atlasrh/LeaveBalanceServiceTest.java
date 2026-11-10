package org.example.atlasrh;

import org.example.atlasrh.Enum.LeaveStatus;
import org.example.atlasrh.mapper.LeaveRequestMapper;
import org.example.atlasrh.model.Employee;
import org.example.atlasrh.model.LeaveRequest;
import org.example.atlasrh.model.LeaveType;
import org.example.atlasrh.repository.EmployeeRepository;
import org.example.atlasrh.repository.LeaveRequestRepository;
import org.example.atlasrh.repository.LeaveTypeRepository;
import org.example.atlasrh.service.LeaveRequestServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.Year;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeaveBalanceServiceTest {

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private LeaveTypeRepository leaveTypeRepository;

    @Mock
    private LeaveRequestMapper leaveRequestMapper;

    @InjectMocks
    private LeaveRequestServiceImpl leaveRequestService;

    @Test
    void calculatesRemainingBalanceFromApprovedWeekdaysInCurrentYear() {
        long employeeId = 9L;
        Employee employee = new Employee();
        LeaveType annualLeave = new LeaveType("Annual", 20L);
        annualLeave.setId(1);
        LeaveType exceptionalLeave = new LeaveType("Exceptional", 5L);
        exceptionalLeave.setId(2);

        Year year = Year.now();
        LeaveRequest approvedAnnual = leaveRequest(
                year.atDay(1).minusDays(10),
                year.atDay(10),
                LeaveStatus.APPROVED,
                annualLeave
        );
        LeaveRequest pendingAnnual = leaveRequest(
                year.atDay(1),
                year.atDay(10),
                LeaveStatus.PENDING,
                annualLeave
        );
        LeaveRequest approvedExceptional = leaveRequest(
                year.atDay(1),
                year.atDay(10),
                LeaveStatus.APPROVED,
                exceptionalLeave
        );

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(leaveRequestRepository.findAllByEmployee_MatriculeAndStatus(
                employeeId, LeaveStatus.APPROVED))
                .thenReturn(List.of(approvedAnnual, approvedExceptional));
        when(leaveTypeRepository.findAll()).thenReturn(List.of(annualLeave, exceptionalLeave));

        var balances = leaveRequestService.getLeaveBalances(employeeId);

        long expectedUsed = workingDaysInCurrentYear(approvedAnnual.getStartDate(), approvedAnnual.getEndDate());
        assertEquals(2, balances.size());
        assertEquals(expectedUsed, balances.get(0).approvedDaysUsed());
        assertEquals(20L - expectedUsed, balances.get(0).remainingDays());
        assertEquals(expectedUsed, balances.get(1).approvedDaysUsed());
        assertEquals(5L - expectedUsed, balances.get(1).remainingDays());
    }

    @Test
    void calculatesLengthOfServiceInYearsAndMonths() {
        long employeeId = 9L;
        LocalDate today = LocalDate.now();
        Employee employee = new Employee();
        employee.setHireDate(today.minusYears(4).minusMonths(7).minusDays(12));
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        var serviceLength = leaveRequestService.getLengthOfService(employeeId);
        Period expected = Period.between(employee.getHireDate(), today);

        assertEquals(expected.getYears(), serviceLength.years());
        assertEquals(expected.getMonths(), serviceLength.months());
    }

    @Test
    void rejectsBalanceLookupForUnknownEmployee() {
        when(employeeRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> leaveRequestService.getLeaveBalances(9L));
    }

    private LeaveRequest leaveRequest(LocalDate startDate, LocalDate endDate,
                                     LeaveStatus status, LeaveType leaveType) {
        LeaveRequest request = new LeaveRequest(startDate, endDate, "Test", status);
        request.setLeavetype(leaveType);
        return request;
    }

    private long workingDaysInCurrentYear(LocalDate startDate, LocalDate endDate) {
        LocalDate start = startDate.isAfter(Year.now().atDay(1)) ? startDate : Year.now().atDay(1);
        LocalDate end = endDate.isBefore(Year.now().atDay(Year.now().length()))
                ? endDate : Year.now().atDay(Year.now().length());
        return start.datesUntil(end.plusDays(1))
                .filter(day -> day.getDayOfWeek() != DayOfWeek.SATURDAY
                        && day.getDayOfWeek() != DayOfWeek.SUNDAY)
                .count();
    }
}
