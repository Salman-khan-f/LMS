package com.example.demo.service;

import com.example.demo.dto.LoanApplicationDTO;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LoanApplicationService {

    private final LoanApplicationRepository loanApplicationRepository;
    private final CustomerRepository customerRepository;
    private final ApprovalWorkflowRepository approvalWorkflowRepository;
    private final LoanRepository loanRepository;

    public LoanApplicationDTO createLoanApplication(LoanApplicationDTO applicationDTO) {
        Customer customer = customerRepository.findById(applicationDTO.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + applicationDTO.getCustomerId()));

        LoanApplication application = convertToEntity(applicationDTO);
        application.setCustomer(customer);

        // Perform smart risk assessment
        performRiskAssessment(application);

        // Check for auto-approval
        checkAutoApprovalEligibility(application);

        LoanApplication savedApplication = loanApplicationRepository.save(application);

        // Create initial approval workflow if not auto-approved
        if (!Boolean.TRUE.equals(application.getAutoApprovalEligible())) {
            createApprovalWorkflow(savedApplication);
        } else {
            // Auto-approve the application
            autoApproveApplication(savedApplication);
        }

        return convertToDTO(savedApplication);
    }

    public Optional<LoanApplicationDTO> getLoanApplicationById(Long id) {
        return loanApplicationRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<LoanApplicationDTO> getAllLoanApplications() {
        return loanApplicationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<LoanApplicationDTO> getLoanApplicationsByCustomer(Long customerId) {
        return loanApplicationRepository.findByCustomerId(customerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<LoanApplicationDTO> getLoanApplicationsByStatus(String status) {
        return loanApplicationRepository.findByStatus(LoanApplication.ApplicationStatus.valueOf(status)).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public LoanApplicationDTO updateApplicationStatus(Long id, String status, String comments) {
        LoanApplication application = loanApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan application not found with id: " + id));

        application.setStatus(LoanApplication.ApplicationStatus.valueOf(status));
        application.setComments(comments);

        if (status.equals("APPROVED")) {
            application.setApprovalDate(LocalDateTime.now());
            createLoanFromApplication(application);
        } else if (status.equals("REJECTED")) {
            application.setRejectionDate(LocalDateTime.now());
        }

        LoanApplication updatedApplication = loanApplicationRepository.save(application);
        return convertToDTO(updatedApplication);
    }

    private void performRiskAssessment(LoanApplication application) {
        Customer customer = application.getCustomer();
        BigDecimal riskScore = BigDecimal.ZERO;

        // Credit score factor (0-100 points)
        if (customer.getCreditScore() != null) {
            if (customer.getCreditScore() >= 750) riskScore = riskScore.add(BigDecimal.valueOf(100));
            else if (customer.getCreditScore() >= 650) riskScore = riskScore.add(BigDecimal.valueOf(75));
            else if (customer.getCreditScore() >= 550) riskScore = riskScore.add(BigDecimal.valueOf(50));
            else riskScore = riskScore.add(BigDecimal.valueOf(25));
        }

        // Income factor (0-50 points)
        if (customer.getAnnualIncome() != null) {
            BigDecimal incomeRatio = application.getLoanAmount().divide(BigDecimal.valueOf(customer.getAnnualIncome()), 4, RoundingMode.HALF_UP);
            if (incomeRatio.compareTo(BigDecimal.valueOf(0.3)) <= 0) riskScore = riskScore.add(BigDecimal.valueOf(50));
            else if (incomeRatio.compareTo(BigDecimal.valueOf(0.5)) <= 0) riskScore = riskScore.add(BigDecimal.valueOf(35));
            else if (incomeRatio.compareTo(BigDecimal.valueOf(0.7)) <= 0) riskScore = riskScore.add(BigDecimal.valueOf(20));
            else riskScore = riskScore.add(BigDecimal.valueOf(5));
        }

        // Loan amount factor (0-30 points)
        if (application.getLoanAmount().compareTo(BigDecimal.valueOf(50000)) <= 0) {
            riskScore = riskScore.add(BigDecimal.valueOf(30));
        } else if (application.getLoanAmount().compareTo(BigDecimal.valueOf(200000)) <= 0) {
            riskScore = riskScore.add(BigDecimal.valueOf(20));
        } else {
            riskScore = riskScore.add(BigDecimal.valueOf(10));
        }

        // Employment factor (0-20 points)
        if ("FULL_TIME".equals(customer.getEmploymentStatus()) || "SELF_EMPLOYED".equals(customer.getEmploymentStatus())) {
            riskScore = riskScore.add(BigDecimal.valueOf(20));
        } else if ("PART_TIME".equals(customer.getEmploymentStatus())) {
            riskScore = riskScore.add(BigDecimal.valueOf(10));
        }

        application.setRiskAssessmentScore(riskScore);

        // Determine risk level
        if (riskScore.compareTo(BigDecimal.valueOf(150)) >= 0) {
            application.setRiskLevel("LOW");
        } else if (riskScore.compareTo(BigDecimal.valueOf(100)) >= 0) {
            application.setRiskLevel("MEDIUM");
        } else {
            application.setRiskLevel("HIGH");
        }
    }

    private void checkAutoApprovalEligibility(LoanApplication application) {
        // Auto-approve if risk score is high enough and loan amount is reasonable
        boolean eligible = application.getRiskAssessmentScore() != null &&
                          application.getRiskAssessmentScore().compareTo(BigDecimal.valueOf(150)) >= 0 &&
                          application.getLoanAmount().compareTo(BigDecimal.valueOf(100000)) <= 0 &&
                          "LOW".equals(application.getRiskLevel());

        application.setAutoApprovalEligible(eligible);
    }

    private void createApprovalWorkflow(LoanApplication application) {
        // Create hierarchical approval workflow
        String[] approverRoles = {"JUNIOR_OFFICER", "SENIOR_OFFICER", "MANAGER"};
        int[] approvalLevels = {1, 2, 3};

        for (int i = 0; i < approverRoles.length; i++) {
            ApprovalWorkflow workflow = new ApprovalWorkflow();
            workflow.setLoanApplication(application);
            workflow.setApproverRole(approverRoles[i]);
            workflow.setApproverName("Auto-assigned " + approverRoles[i].toLowerCase().replace("_", " "));
            workflow.setApprovalLevel(approvalLevels[i]);
            approvalWorkflowRepository.save(workflow);
        }
    }

    private void autoApproveApplication(LoanApplication application) {
        application.setStatus(LoanApplication.ApplicationStatus.APPROVED);
        application.setApprovalDate(LocalDateTime.now());
        application.setComments("Auto-approved based on risk assessment score");

        // Create auto-approval workflow entry
        ApprovalWorkflow workflow = new ApprovalWorkflow();
        workflow.setLoanApplication(application);
        workflow.setApproverRole("SYSTEM");
        workflow.setApproverName("Automated Approval System");
        workflow.setStatus(ApprovalWorkflow.ApprovalStatus.AUTO_APPROVED);
        workflow.setApprovalLevel(0);
        workflow.setIsAutoApproved(true);
        workflow.setReviewedDate(LocalDateTime.now());
        workflow.setApprovedAmount(application.getLoanAmount());
        workflow.setApprovedInterestRate(application.getInterestRate());
        approvalWorkflowRepository.save(workflow);

        // Create the loan
        createLoanFromApplication(application);
    }

    private void createLoanFromApplication(LoanApplication application) {
        Loan loan = new Loan();
        loan.setLoanApplication(application);
        loan.setLoanNumber("LN" + System.currentTimeMillis());
        loan.setPrincipalAmount(application.getLoanAmount());
        loan.setOutstandingBalance(application.getLoanAmount());
        loan.setInterestRate(application.getInterestRate());
        loan.setTermMonths(application.getLoanTermMonths());
        loan.setStartDate(java.time.LocalDate.now());
        loan.setEndDate(java.time.LocalDate.now().plusMonths(application.getLoanTermMonths()));

        // Calculate monthly payment using simple interest formula
        BigDecimal monthlyRate = application.getInterestRate().divide(BigDecimal.valueOf(100 * 12), 10, RoundingMode.HALF_UP);
        BigDecimal monthlyPayment = application.getLoanAmount().multiply(monthlyRate).divide(
            BigDecimal.ONE.subtract(BigDecimal.ONE.divide(BigDecimal.ONE.add(monthlyRate).pow(application.getLoanTermMonths()), 10, RoundingMode.HALF_UP)), 2, RoundingMode.HALF_UP);
        loan.setMonthlyPayment(monthlyPayment);

        loanRepository.save(loan);
    }

    private LoanApplicationDTO convertToDTO(LoanApplication application) {
        LoanApplicationDTO dto = new LoanApplicationDTO();
        dto.setId(application.getId());
        dto.setCustomerId(application.getCustomer().getId());
        dto.setCustomerName(application.getCustomer().getFirstName() + " " + application.getCustomer().getLastName());
        dto.setLoanType(application.getLoanType());
        dto.setLoanAmount(application.getLoanAmount());
        dto.setLoanTermMonths(application.getLoanTermMonths());
        dto.setInterestRate(application.getInterestRate());
        dto.setPurpose(application.getPurpose());
        dto.setStatus(application.getStatus().toString());
        dto.setApplicationDate(application.getApplicationDate());
        dto.setApprovalDate(application.getApprovalDate());
        dto.setRejectionDate(application.getRejectionDate());
        dto.setComments(application.getComments());
        dto.setCreditScore(application.getCreditScore());
        dto.setRiskAssessmentScore(application.getRiskAssessmentScore());
        dto.setRiskLevel(application.getRiskLevel());
        dto.setAutoApprovalEligible(application.getAutoApprovalEligible());

        // Get current approval level
        List<ApprovalWorkflow> workflows = approvalWorkflowRepository.findByLoanApplicationIdOrderByApprovalLevel(application.getId());
        if (!workflows.isEmpty()) {
            ApprovalWorkflow currentWorkflow = workflows.stream()
                    .filter(w -> w.getStatus() == ApprovalWorkflow.ApprovalStatus.PENDING)
                    .findFirst()
                    .orElse(workflows.get(workflows.size() - 1));
            dto.setCurrentApprovalLevel(currentWorkflow.getApprovalLevel());
            dto.setNextApproverRole(currentWorkflow.getApproverRole());
        }

        return dto;
    }

    private LoanApplication convertToEntity(LoanApplicationDTO dto) {
        LoanApplication application = new LoanApplication();
        application.setLoanType(dto.getLoanType());
        application.setLoanAmount(dto.getLoanAmount());
        application.setLoanTermMonths(dto.getLoanTermMonths());
        application.setInterestRate(dto.getInterestRate());
        application.setPurpose(dto.getPurpose());
        application.setComments(dto.getComments());
        return application;
    }
}