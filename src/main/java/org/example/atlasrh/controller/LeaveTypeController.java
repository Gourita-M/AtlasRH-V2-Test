package org.example.atlasrh.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.atlasrh.model.LeaveType;
import org.example.atlasrh.service.LeaveTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leavetype")
@Tag(name = "Department", description = "Department management")
public class LeaveTypeController {
    private final LeaveTypeService leaveTypeService;

    public LeaveTypeController(LeaveTypeService leaveTypeService) {
        this.leaveTypeService = leaveTypeService;
    }

    //PostMan done
    @PostMapping
    @Operation(summary = "Create LeaveType")
    public ResponseEntity<?> create(@RequestBody LeaveType leaveType) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(leaveTypeService.create(leaveType));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //PostMan done
    @GetMapping("/{id}")
    @Operation(summary = "View LeaveType By ID")
    public ResponseEntity<?> viewLeaveType(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(leaveTypeService.viewLeaveType(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    //PostMan done
    @GetMapping
    @Operation(summary = "View All LeaveType")
    public ResponseEntity<?> viewAll() {
        try {
            return ResponseEntity.ok(leaveTypeService.viewAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
