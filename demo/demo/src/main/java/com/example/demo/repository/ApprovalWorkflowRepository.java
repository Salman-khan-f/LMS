package com.example.demo.repository;

import com.example.demo.entity.ApprovalWorkflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ApprovalWorkflowRepository extends JpaRepository<ApprovalWorkflow, Long> {

    List<ApprovalWorkflow> findByLoanApplicationId(Long loanApplicationId);

    List<ApprovalWorkflow> findByApproverRole(String approverRole);

    List<ApprovalWorkflow> findByStatus(ApprovalWorkflow.ApprovalStatus status);

    @Query("SELECT aw FROM ApprovalWorkflow aw WHERE aw.loanApplication.id = :applicationId ORDER BY aw.approvalLevel ASC")
    List<ApprovalWorkflow> findByLoanApplicationIdOrderByApprovalLevel(@Param("applicationId") Long applicationId);

    @Query("SELECT aw FROM ApprovalWorkflow aw WHERE aw.status = 'PENDING' AND aw.approverRole = :role")
    List<ApprovalWorkflow> findPendingApprovalsByRole(@Param("role") String role);

    @Query("SELECT COUNT(aw) FROM ApprovalWorkflow aw WHERE aw.status = :status AND aw.assignedDate >= :startDate")
    Long countByStatusAndDateAfter(@Param("status") ApprovalWorkflow.ApprovalStatus status,
                                 @Param("startDate") java.time.LocalDateTime startDate);
}