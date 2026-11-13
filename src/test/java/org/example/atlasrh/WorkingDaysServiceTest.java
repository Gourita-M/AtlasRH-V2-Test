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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkingDaysServiceTest {
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

    @ParameterizedTest(name = "leave from day offset {0} to {1} counts {2} working days")
    @CsvSource({
            "0, 4, 5",
            "0, 6, 5",
            "5, 6, 0",
            "4, 7, 2"
    })
    void countsWeekdaysExcludingWeekends(int startOffset, int endOffset, long expectedWorkingDays) {
        long employeeId = 9L;
        Employee employee = new Employee();
        LeaveType leaveType = new LeaveType("Annual", 25L);
        leaveType.setId(1);
        LocalDate firstMonday = Year.now().atDay(1)
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
        LeaveRequest approvedRequest = new LeaveRequest(
                firstMonday.plusDays(startOffset),
                firstMonday.plusDays(endOffset),
                "Test leave",
                LeaveStatus.APPROVED);
        approvedRequest.setLeavetype(leaveType);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(leaveRequestRepository.findAllByEmployee_MatriculeAndStatus(
                employeeId, LeaveStatus.APPROVED)).thenReturn(List.of(approvedRequest));
        when(leaveTypeRepository.findAll()).thenReturn(List.of(leaveType));

        var balance = leaveRequestService.getLeaveBalances(employeeId).get(0);

        assertEquals(expectedWorkingDays, balance.approvedDaysUsed());
        assertEquals(25L - expectedWorkingDays, balance.remainingDays());
    }
}
