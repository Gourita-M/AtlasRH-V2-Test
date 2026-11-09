package org.example.atlasrh.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "department")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column( nullable = false)
    private String name;

    @Column( nullable = false)
    private String description;

    @OneToMany
    List<Employee> employees = new ArrayList<>();

    public Department() {}
    public Department(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Employee employee) {
        this.employees.add(employee);
    }
}
