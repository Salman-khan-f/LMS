package com.example.demo.controller;

import com.example.demo.entity.ApprovalWorkflow;
import com.example.demo.repository.ApprovalWorkflowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/approvals")
@RequiredArgsConstructor
public class ApprovalWorkflowController {

    private final ApprovalWorkflowRepository approvalWorkflowRepository;

    @GetMapping
    public ResponseEntity<List<ApprovalWorkflow>> getAllApprovals() {
        List<ApprovalWorkflow> approvals = approvalWorkflowRepository.findAll();
        return new ResponseEntity<>(approvals, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApprovalWorkflow> getApprovalById(@PathVariable Long id) {
        Optional<ApprovalWorkflow> approval = approvalWorkflowRepository.findById(id);
        return approval.map(a -> new ResponseEntity<>(a, HttpStatus.OK))
                       .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<List<ApprovalWorkflow>> getApprovalsByApplication(@PathVariable Long applicationId) {
        List<ApprovalWorkflow> approvals = approvalWorkflowRepository.findByLoanApplicationIdOrderByApprovalLevel(applicationId);
        return new ResponseEntity<>(approvals, HttpStatus.OK);
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<ApprovalWorkflow>> getApprovalsByRole(@PathVariable String role) {
        List<ApprovalWorkflow> approvals = approvalWorkflowRepository.findByApproverRole(role);
        return new ResponseEntity<>(approvals, HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ApprovalWorkflow>> getApprovalsByStatus(@PathVariable String status) {
        try {
            ApprovalWorkflow.ApprovalStatus approvalStatus = ApprovalWorkflow.ApprovalStatus.valueOf(status.toUpperCase());
            List<ApprovalWorkflow> approvals = approvalWorkflowRepository.findByStatus(approvalStatus);
            return new ResponseEntity<>(approvals, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/pending/role/{role}")
    public ResponseEntity<List<ApprovalWorkflow>> getPendingApprovalsByRole(@PathVariable String role) {
        List<ApprovalWorkflow> approvals = approvalWorkflowRepository.findPendingApprovalsByRole(role);
        return new ResponseEntity<>(approvals, HttpStatus.OK);
    }

    @PutMapping("/{id}/review")
    public ResponseEntity<ApprovalWorkflow> reviewApproval(@PathVariable Long id, @RequestBody ApprovalReviewDTO review) {
        Optional<ApprovalWorkflow> optionalApproval = approvalWorkflowRepository.findById(id);
        if (optionalApproval.isPresent()) {
            ApprovalWorkflow approval = optionalApproval.get();

            if (approval.getStatus() != ApprovalWorkflow.ApprovalStatus.PENDING) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            try {
                ApprovalWorkflow.ApprovalStatus newStatus = ApprovalWorkflow.ApprovalStatus.valueOf(review.getStatus().toUpperCase());
                approval.setStatus(newStatus);
                approval.setComments(review.getComments());
                approval.setReviewedDate(java.time.LocalDateTime.now());

                if (newStatus == ApprovalWorkflow.ApprovalStatus.APPROVED) {
                    approval.setApprovedAmount(review.getApprovedAmount());
                    approval.setApprovedInterestRate(review.getApprovedInterestRate());
                }

                ApprovalWorkflow updatedApproval = approvalWorkflowRepository.save(approval);
                return new ResponseEntity<>(updatedApproval, HttpStatus.OK);
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/dashboard/summary")
    public ResponseEntity<ApprovalSummaryDTO> getApprovalSummary() {
        Long totalApprovals = approvalWorkflowRepository.count();
        Long pendingApprovals = approvalWorkflowRepository.countByStatusAndDateAfter(
            ApprovalWorkflow.ApprovalStatus.PENDING, java.time.LocalDateTime.now().minusDays(30));
        Long approvedToday = approvalWorkflowRepository.countByStatusAndDateAfter(
            ApprovalWorkflow.ApprovalStatus.APPROVED, java.time.LocalDateTime.now().toLocalDate().atStartOfDay());

        ApprovalSummaryDTO summary = new ApprovalSummaryDTO(totalApprovals, pendingApprovals, approvedToday);
        return new ResponseEntity<>(summary, HttpStatus.OK);
    }

    public static class ApprovalReviewDTO {
        private String status;
        private String comments;
        private java.math.BigDecimal approvedAmount;
        private java.math.BigDecimal approvedInterestRate;

        // Getters and setters
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getComments() { return comments; }
        public void setComments(String comments) { this.comments = comments; }
        public java.math.BigDecimal getApprovedAmount() { return approvedAmount; }
        public void setApprovedAmount(java.math.BigDecimal approvedAmount) { this.approvedAmount = approvedAmount; }
        public java.math.BigDecimal getApprovedInterestRate() { return approvedInterestRate; }
        public void setApprovedInterestRate(java.math.BigDecimal approvedInterestRate) { this.approvedInterestRate = approvedInterestRate; }
    }

    public static class ApprovalSummaryDTO {
        private Long totalApprovals;
        private Long pendingApprovals;
        private Long approvedToday;

        public ApprovalSummaryDTO(Long totalApprovals, Long pendingApprovals, Long approvedToday) {
            this.totalApprovals = totalApprovals;
            this.pendingApprovals = pendingApprovals;
            this.approvedToday = approvedToday;
        }

        // Getters
        public Long getTotalApprovals() { return totalApprovals; }
        public Long getPendingApprovals() { return pendingApprovals; }
        public Long getApprovedToday() { return approvedToday; }
    }
}