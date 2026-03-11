package com.example.demo.repository;

import com.example.demo.entity.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {

    List<LoanApplication> findByCustomerId(Long customerId);

    List<LoanApplication> findByStatus(LoanApplication.ApplicationStatus status);

    List<LoanApplication> findByLoanType(String loanType);

    @Query("SELECT la FROM LoanApplication la WHERE la.status = :status AND la.applicationDate >= :startDate")
    List<LoanApplication> findByStatusAndDateAfter(@Param("status") LoanApplication.ApplicationStatus status,
                                                 @Param("startDate") LocalDateTime startDate);

    @Query("SELECT la FROM LoanApplication la WHERE la.loanAmount >= :minAmount AND la.loanAmount <= :maxAmount")
    List<LoanApplication> findByLoanAmountRange(@Param("minAmount") BigDecimal minAmount,
                                              @Param("maxAmount") BigDecimal maxAmount);

    @Query("SELECT COUNT(la) FROM LoanApplication la WHERE la.status = :status")
    Long countByStatus(@Param("status") LoanApplication.ApplicationStatus status);

    @Query("SELECT la FROM LoanApplication la WHERE la.riskLevel = :riskLevel AND la.status = 'PENDING'")
    List<LoanApplication> findPendingApplicationsByRiskLevel(@Param("riskLevel") String riskLevel);

    @Query("SELECT la FROM LoanApplication la WHERE la.autoApprovalEligible = true AND la.status = 'PENDING'")
    List<LoanApplication> findAutoApprovalEligibleApplications();
}