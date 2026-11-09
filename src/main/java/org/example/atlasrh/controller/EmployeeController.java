package org.example.atlasrh.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.atlasrh.dto.EmployeeResponseDTO;
import org.example.atlasrh.model.Employee;
import org.example.atlasrh.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
@Tag(name = "Employee", description = "Employee Management")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService){
        this.employeeService = employeeService;
    }

    //PostMan done
    @GetMapping
    @Operation(summary = "Get All Employs")
    public ResponseEntity<List<EmployeeResponseDTO>> getAll(){

        return ResponseEntity.ok(employeeService.viewAll());
    }

    //PostMan done
    @PostMapping("/{id}")
    @Operation(summary = "Create Employee")
    public ResponseEntity<?> create(@RequestBody Employee employee, @PathVariable("id") Long id){
        try {
            Employee created = employeeService.create(employee, id);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(created);

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        }
    }

    //PostMan done
    @GetMapping("/{id}")
    @Operation(summary = "Get Employee By ID")
    public ResponseEntity<?> viewEmployee(@PathVariable("id") Long id){
        try{
            return ResponseEntity.ok(employeeService.viewEmployee(id));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
