package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "approval_workflows")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalWorkflow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_application_id", nullable = false)
    private LoanApplication loanApplication;

    @NotBlank(message = "Approver role is required")
    private String approverRole; // JUNIOR_OFFICER, SENIOR_OFFICER, MANAGER, DIRECTOR

    @NotBlank(message = "Approver name is required")
    private String approverName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus status = ApprovalStatus.PENDING;

    @Size(max = 1000, message = "Comments cannot exceed 1000 characters")
    private String comments;

    @Column(nullable = false)
    private LocalDateTime assignedDate = LocalDateTime.now();

    private LocalDateTime reviewedDate;

    private Integer approvalLevel; // 1, 2, 3, 4 for hierarchical approval

    @DecimalMin(value = "0.00", message = "Approved amount must be non-negative")
    private java.math.BigDecimal approvedAmount;

    @DecimalMin(value = "0.00", message = "Approved interest rate must be non-negative")
    private java.math.BigDecimal approvedInterestRate;

    private Boolean isAutoApproved = false;

    public enum ApprovalStatus {
        PENDING,
        APPROVED,
        REJECTED,
        ESCALATED,
        AUTO_APPROVED
    }

    @PrePersist
    public void prePersist() {
        if (this.assignedDate == null) {
            this.assignedDate = LocalDateTime.now();
        }
    }
}