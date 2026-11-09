package org.example.atlasrh.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contract")
@Setter
@Getter
@ToString
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "\"contractType\"" , nullable = false)
    private ContractType contractType;

    @Column(name = "\"startDate\"" , nullable = false)
    private LocalDate startDate;

    @Column(name = "\"endDate\"" , nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Long salary;

    @OneToMany
    @Setter(AccessLevel.NONE)
    List<Employee> employee = new ArrayList<>();

    public Contract(){}
    public Contract(ContractType contractType, LocalDate startDate, LocalDate endDate, Long salary) {
        this.contractType = contractType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.salary = salary;
    }

    public enum ContractType{
        CDI,
        CDD,
        STAGE
    }

    public void setEmployee(Employee employee) {
        this.employee.add(employee);
    }
}
