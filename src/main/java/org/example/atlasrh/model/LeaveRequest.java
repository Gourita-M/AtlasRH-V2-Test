package org.example.atlasrh.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.example.atlasrh.Enum.LeaveStatus;

import java.time.LocalDate;

@Entity
@Table(name = "leaverequest")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class LeaveRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(name = "\"startDate\"" , nullable = false)
    private LocalDate startDate;
    @NotNull
    @Column(name = "\"endDate\"" , nullable = false)
    private LocalDate endDate;
    @NotBlank
    @Column(nullable = false)
    private String reason;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveStatus status;
    @Column(name = "decision_comment")
    private String decisionComment;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    Employee employee;

    @ManyToOne
    LeaveType leavetype;

    public LeaveRequest(LocalDate startDate, LocalDate endDate, String reason, LeaveStatus status) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = status;
    }
}
