package org.example.atlasrh.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.atlasrh.dto.DepartmentResponseDTO;
import org.example.atlasrh.dto.DepartmentRequestDTO;
import org.example.atlasrh.service.DepartmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/department")
@Tag(name = "Department", description = "Department Management")
public class DepartmentController {
    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    //PostMan done
    @GetMapping
    @Operation(summary = "Get all Departments")
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(departmentService.viewAll());
    }

    //PostMan done
    @PostMapping
    @Operation(summary = "Create Department")
    public ResponseEntity<?> save(@Valid @RequestBody DepartmentRequestDTO request) {
        try{
            DepartmentResponseDTO dto = departmentService.create(request.toDepartment());
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an empty department")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            departmentService.deleteDepartment(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
