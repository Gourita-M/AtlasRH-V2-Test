package org.example.atlasrh.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    @NotBlank
    @Column(name = "\"firstName\"" , nullable = false)
    private String firstName;

    @NotBlank
    @Column(name = "\"lastName\"" , nullable = false)
    private String lastName;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(name = "\"jobTitle\"" , nullable = false)
    private String jobTitle;

    @NotNull
    @Column(name = "\"hireDate\"" , nullable = false)
    private LocalDate hireDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column( nullable = false )
    private RoleType role;

    @ManyToOne
    @JoinColumn( name = "department_id" )
    Department department;

    @ManyToOne
    @JoinColumn(name = "contract_id")
    Contract contract;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Employee manager;

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

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public Employee getManager() {
        return manager;
    }

    public void setManager(Employee manager) {
        this.manager = manager;
    }
}
