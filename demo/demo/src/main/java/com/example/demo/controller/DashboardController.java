package com.example.demo.controller;

import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final CustomerRepository customerRepository;
    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanRepository loanRepository;
    private final PaymentRepository paymentRepository;
    private final ApprovalWorkflowRepository approvalWorkflowRepository;

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getDashboardSummary() {
        Map<String, Object> summary = new HashMap<>();

        // Customer statistics
        Long totalCustomers = customerRepository.count();
        Long customersThisMonth = customerRepository.countCustomersCreatedBetween(
            LocalDate.now().withDayOfMonth(1), LocalDate.now());

        // Loan application statistics
        Long totalApplications = loanApplicationRepository.count();
        Long pendingApplications = loanApplicationRepository.countByStatus(
            com.example.demo.entity.LoanApplication.ApplicationStatus.PENDING);
        Long approvedApplications = loanApplicationRepository.countByStatus(
            com.example.demo.entity.LoanApplication.ApplicationStatus.APPROVED);
        Long rejectedApplications = loanApplicationRepository.countByStatus(
            com.example.demo.entity.LoanApplication.ApplicationStatus.REJECTED);

        // Loan statistics
        Long totalLoans = loanRepository.count();
        Long activeLoans = loanRepository.countByStatus(
            com.example.demo.entity.Loan.LoanStatus.ACTIVE);
        BigDecimal totalOutstandingBalance = loanRepository.getTotalOutstandingBalance();
        if (totalOutstandingBalance == null) {
            totalOutstandingBalance = BigDecimal.ZERO;
        }

        // Payment statistics (last 30 days)
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        BigDecimal recentPayments = paymentRepository.getTotalPaymentsInDateRange(
            thirtyDaysAgo, LocalDate.now());
        if (recentPayments == null) {
            recentPayments = BigDecimal.ZERO;
        }

        // Approval workflow statistics
        Long totalApprovals = approvalWorkflowRepository.count();
        Long pendingApprovals = approvalWorkflowRepository.countByStatusAndDateAfter(
            com.example.demo.entity.ApprovalWorkflow.ApprovalStatus.PENDING,
            java.time.LocalDateTime.now().minusDays(30));

        // Risk assessment summary
        Long lowRiskApplications = (long) loanApplicationRepository.findPendingApplicationsByRiskLevel("LOW").size();
        Long mediumRiskApplications = (long) loanApplicationRepository.findPendingApplicationsByRiskLevel("MEDIUM").size();
        Long highRiskApplications = (long) loanApplicationRepository.findPendingApplicationsByRiskLevel("HIGH").size();
        Long autoApprovalEligible = (long) loanApplicationRepository.findAutoApprovalEligibleApplications().size();

        Map<String, Object> customersMap = new HashMap<>();
        customersMap.put("total", totalCustomers);
        customersMap.put("newThisMonth", customersThisMonth);
        summary.put("customers", customersMap);

        Map<String, Object> applicationsMap = new HashMap<>();
        applicationsMap.put("total", totalApplications);
        applicationsMap.put("pending", pendingApplications);
        applicationsMap.put("approved", approvedApplications);
        applicationsMap.put("rejected", rejectedApplications);
        summary.put("applications", applicationsMap);

        Map<String, Object> loansMap = new HashMap<>();
        loansMap.put("total", totalLoans);
        loansMap.put("active", activeLoans);
        loansMap.put("totalOutstandingBalance", totalOutstandingBalance);
        summary.put("loans", loansMap);

        Map<String, Object> paymentsMap = new HashMap<>();
        paymentsMap.put("recentPayments", recentPayments);
        summary.put("payments", paymentsMap);

        Map<String, Object> approvalsMap = new HashMap<>();
        approvalsMap.put("total", totalApprovals);
        approvalsMap.put("pending", pendingApprovals);
        summary.put("approvals", approvalsMap);

        Map<String, Object> riskMap = new HashMap<>();
        riskMap.put("lowRisk", lowRiskApplications);
        riskMap.put("mediumRisk", mediumRiskApplications);
        riskMap.put("highRisk", highRiskApplications);
        riskMap.put("autoApprovalEligible", autoApprovalEligible);
        summary.put("riskAssessment", riskMap);

        return new ResponseEntity<>(summary, HttpStatus.OK);
    }

    @GetMapping("/risk-analysis")
    public ResponseEntity<Map<String, Object>> getRiskAnalysis() {
        Map<String, Object> analysis = new HashMap<>();

        // Average credit scores by risk level
        // This would require custom queries - for now, return basic stats
        Long totalLowRisk = (long) loanApplicationRepository.findPendingApplicationsByRiskLevel("LOW").size();
        Long totalMediumRisk = (long) loanApplicationRepository.findPendingApplicationsByRiskLevel("MEDIUM").size();
        Long totalHighRisk = (long) loanApplicationRepository.findPendingApplicationsByRiskLevel("HIGH").size();

        analysis.put("riskDistribution", Map.of(
            "low", totalLowRisk,
            "medium", totalMediumRisk,
            "high", totalHighRisk
        ));

        analysis.put("autoApprovalRate", loanApplicationRepository.findAutoApprovalEligibleApplications().size());

        return new ResponseEntity<>(analysis, HttpStatus.OK);
    }
}