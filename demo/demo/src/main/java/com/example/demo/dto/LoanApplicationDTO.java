package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplicationDTO {

    private Long id;
    private Long customerId;
    private String customerName;
    private String loanType;
    private BigDecimal loanAmount;
    private Integer loanTermMonths;
    private BigDecimal interestRate;
    private String purpose;
    private String status;
    private LocalDateTime applicationDate;
    private LocalDateTime approvalDate;
    private LocalDateTime rejectionDate;
    private String comments;
    private BigDecimal creditScore;
    private BigDecimal riskAssessmentScore;
    private String riskLevel;
    private Boolean autoApprovalEligible;
    private Integer currentApprovalLevel;
    private String nextApproverRole;
}