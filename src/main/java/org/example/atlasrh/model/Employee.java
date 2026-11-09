package org.example.atlasrh.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.example.atlasrh.Enum.RoleType;

@Entity
@Table( name = "employee" )
public class Employee {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long matricule;

    @Column(name = "\"firstName\"" , nullable = false)
    private String firstName;

    @Column(name = "\"lastName\"" , nullable = false)
    private String lastName;

    @Column( nullable = false )
    private String email;

    @Column(name = "\"jobTitle\"" , nullable = false)
    private String jobTitle;

    @Column(name = "\"hireDate\"" , nullable = false)
    private LocalDate hireDate;

    @Enumerated(EnumType.STRING)
    @Column( nullable = false )
    private RoleType role;

    @ManyToOne
    @JoinColumn( name = "department_id" )
    Department department;

    @ManyToOne
    @JoinColumn( name = "employee_id" )
    Contract contract;

    @OneToMany
    List<LeaveRequest> leaveRequests = new ArrayList<>();

    public Employee(){

    }

    public Employee(String firstName, String lastName, String email, String jobTitle, LocalDate hireDate, RoleType role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.jobTitle = jobTitle;
        this.hireDate = hireDate;
        this.role = role;
    }

    public long getMatricule() {
        return matricule;
    }

    public void setMatricule(long matricule) {
        this.matricule = matricule;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }


}
