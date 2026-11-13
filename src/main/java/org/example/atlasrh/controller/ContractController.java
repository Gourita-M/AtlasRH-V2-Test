package org.example.atlasrh.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.atlasrh.dto.ContractRequestDTO;
import org.example.atlasrh.service.ContractService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contract")
@Tag(name = "Contract", description = "Contract management")
public class ContractController {
    private final ContractService contractService;

    public ContractController(ContractService contractService){
        this.contractService = contractService;
    }

    //PostMan done
    @GetMapping
    @Operation(summary = "Get all Contracts")
    public ResponseEntity<?> getAll(){
        try{
            return ResponseEntity.ok(contractService.viewAll());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //PostMan done
    @PostMapping("/{id}")
    @Operation(summary = "Create Contract")
    public ResponseEntity<?> save(@Valid @RequestBody ContractRequestDTO request,
                                  @PathVariable("id") Long id){
        try{
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(contractService.create(request.toContract(), id));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
