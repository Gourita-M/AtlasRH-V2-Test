package org.example.atlasrh.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "leavetype")
@Setter
@Getter
@NoArgsConstructor
@ToString
public class LeaveType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Long annualallowancedays;

    @OneToMany
    @JoinColumn(name = "leavetype_id")
    List<LeaveRequest> leaverequest = new ArrayList<>();

    public LeaveType(String name, Long annualallowancedays) {
        this.name = name;
        this.annualallowancedays = annualallowancedays;
    }
}
