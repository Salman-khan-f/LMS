package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "loan_applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotBlank(message = "Loan type is required")
    private String loanType; // PERSONAL, HOME, AUTO, BUSINESS

    @NotNull(message = "Loan amount is required")
    @DecimalMin(value = "10000.00", message = "Loan amount must be at least 10,000")
    @DecimalMax(value = "500000.00", message = "Loan amount cannot exceed 500,000")
    private BigDecimal loanAmount;

    @NotNull(message = "Loan term is required")
    @Min(value = 6, message = "Loan term must be at least 6 months")
    @Max(value = 360, message = "Loan term cannot exceed 30 years (360 months)")
    private Integer loanTermMonths;  // tenure in months

    @NotNull(message = "Interest rate is required")
    @DecimalMin(value = "0.01", message = "Interest rate must be at least 0.01%")
    @DecimalMax(value = "30.00", message = "Interest rate cannot exceed 30%")
    private BigDecimal interestRate;

    @NotBlank(message = "Purpose is required")
    @Size(max = 500, message = "Purpose cannot exceed 500 characters")
    private String purpose;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime applicationDate = LocalDateTime.now();

    private LocalDateTime approvalDate;
    private LocalDateTime rejectionDate;

    @Size(max = 1000, message = "Comments cannot exceed 1000 characters")
    private String comments;

    // Smart scoring fields
    private BigDecimal creditScore;
    private BigDecimal riskAssessmentScore;
    private String riskLevel; // LOW, MEDIUM, HIGH
    private Boolean autoApprovalEligible;

    @OneToMany(mappedBy = "loanApplication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ApprovalWorkflow> approvalWorkflows;

    @OneToOne(mappedBy = "loanApplication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Loan loan;

    public enum ApplicationStatus {
        PENDING,
        UNDER_REVIEW,
        APPROVED,
        REJECTED,
        WITHDRAWN
    }

    @PrePersist
    public void prePersist() {
        if (this.applicationDate == null) {
            this.applicationDate = LocalDateTime.now();
        }
    }
}