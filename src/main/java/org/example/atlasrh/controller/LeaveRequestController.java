package org.example.atlasrh.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.atlasrh.Enum.LeaveStatus;
import org.example.atlasrh.model.LeaveRequest;
import org.example.atlasrh.service.LeaveRequestService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leaverequest")
@Tag(name = "LeaveRequest", description = "LeaveRequest management")
public class LeaveRequestController {
    private final LeaveRequestService leaveRequestService;

    public LeaveRequestController(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    //PostMan done
    @PostMapping
    @Operation(summary = "Create LeaveRequest")
    public ResponseEntity<?> create(@RequestBody LeaveRequest leaveRequest,
                                    @RequestParam Long employeeId,
                                    @RequestParam Long leaveTypeId) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(leaveRequestService.create(leaveRequest, employeeId, leaveTypeId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        }
    }

    //PostMan done
    @PutMapping("/{id}")
    @Operation(summary = "Update LeaveRequest")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody LeaveRequest leaveRequest) {
        try {
            return ResponseEntity.ok(leaveRequestService.update(id, leaveRequest));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //PostMan done
    @GetMapping("/{id}")
    @Operation(summary = "View LeaveRequest By ID")
    public ResponseEntity<?> viewLeaveRequest(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(leaveRequestService.viewLeaveRequest(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    //PostMan done
    @GetMapping
    @Operation(summary = "View All LeaveRequest")
    public ResponseEntity<?> viewAll() {
        try {
            return ResponseEntity.ok(leaveRequestService.viewAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    //PostMan done
    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get Requests By Employee ID")
    public ResponseEntity<?> getRequests(@PathVariable Long employeeId, Pageable pageable) {
        try {
            return ResponseEntity.ok(leaveRequestService.getRequests(employeeId, pageable));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    //PostMan done
    @GetMapping("/employee/{employeeId}/status")
    @Operation(summary = "View LeaveRequest By Status")
    public ResponseEntity<?> getMyRequestsByStatus(@PathVariable Long employeeId,
                                                  @RequestParam LeaveStatus status,
                                                  Pageable pageable) {
        try {
            return ResponseEntity.ok(leaveRequestService.getMyRequestsByStatus(employeeId, status, pageable));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //PostMan done
    @PutMapping("/cancel")
    @Operation(summary = "Cancel LeaveRequest By ID")
    public ResponseEntity<?> cancelRequest(@RequestParam Long employeeId, @RequestParam Long requestId) {
        try {
            boolean cancelled = leaveRequestService.cancelRequest(employeeId, requestId);
            if (cancelled) {
                return ResponseEntity.ok("Leave request cancelled successfully");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to cancel this leave request");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //PostMan done
    @PutMapping("/{requestId}/approve")
    @Operation(summary = "Approve LeaveRequest By ID")
    public ResponseEntity<?> approveRequest(@RequestParam Long managerId,
                                           @PathVariable Long requestId,
                                           @RequestParam(required = false) String comment) {
        try {
            boolean approved = leaveRequestService.approveRequest(managerId, requestId, comment);
            if (approved) {
                return ResponseEntity.ok("Leave request approved successfully");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to approve this leave request");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //PostMan done
    @PutMapping("/{requestId}/reject")
    @Operation(summary = "Reject LeaveRequest By ID")
    public ResponseEntity<?> rejectRequest(@RequestParam Long managerId,
                                          @PathVariable Long requestId,
                                          @RequestParam(required = false) String comment) {
        try {
            boolean rejected = leaveRequestService.rejectRequest(managerId, requestId, comment);
            if (rejected) {
                return ResponseEntity.ok("Leave request rejected successfully");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to reject this leave request");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}


