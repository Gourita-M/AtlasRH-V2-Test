package org.example.atlasrh.repository;

import org.example.atlasrh.model.LeaveRequest;
import org.example.atlasrh.Enum.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    LeaveRequest findByIdAndEmployee_Matricule(Long requestId, long employeeId);

    List<LeaveRequest> findAllByEmployee_MatriculeAndStatus(long employeeId, LeaveStatus status);
}
