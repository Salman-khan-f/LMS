package com.example.demo.controller;

import com.example.demo.dto.LoanApplicationDTO;
import com.example.demo.service.LoanApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loan-applications")
@RequiredArgsConstructor
public class LoanApplicationController {

    private final LoanApplicationService loanApplicationService;

    @PostMapping
    public ResponseEntity<LoanApplicationDTO> createLoanApplication(@Valid @RequestBody LoanApplicationDTO applicationDTO) {
        LoanApplicationDTO createdApplication = loanApplicationService.createLoanApplication(applicationDTO);
        return new ResponseEntity<>(createdApplication, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanApplicationDTO> getLoanApplicationById(@PathVariable Long id) {
        return loanApplicationService.getLoanApplicationById(id)
                .map(application -> new ResponseEntity<>(application, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<LoanApplicationDTO>> getAllLoanApplications() {
        List<LoanApplicationDTO> applications = loanApplicationService.getAllLoanApplications();
        return new ResponseEntity<>(applications, HttpStatus.OK);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<LoanApplicationDTO>> getLoanApplicationsByCustomer(@PathVariable Long customerId) {
        List<LoanApplicationDTO> applications = loanApplicationService.getLoanApplicationsByCustomer(customerId);
        return new ResponseEntity<>(applications, HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<LoanApplicationDTO>> getLoanApplicationsByStatus(@PathVariable String status) {
        List<LoanApplicationDTO> applications = loanApplicationService.getLoanApplicationsByStatus(status);
        return new ResponseEntity<>(applications, HttpStatus.OK);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<LoanApplicationDTO> updateApplicationStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusUpdate) {
        try {
            String status = statusUpdate.get("status");
            String comments = statusUpdate.get("comments");
            LoanApplicationDTO updatedApplication = loanApplicationService.updateApplicationStatus(id, status, comments);
            return new ResponseEntity<>(updatedApplication, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/auto-approval-eligible")
    public ResponseEntity<List<LoanApplicationDTO>> getAutoApprovalEligibleApplications() {
        // This would need to be implemented in the service
        // For now, return empty list
        return new ResponseEntity<>(List.of(), HttpStatus.OK);
    }

    @GetMapping("/risk-level/{riskLevel}")
    public ResponseEntity<List<LoanApplicationDTO>> getApplicationsByRiskLevel(@PathVariable String riskLevel) {
        // This would need to be implemented in the service
        // For now, return empty list
        return new ResponseEntity<>(List.of(), HttpStatus.OK);
    }
}