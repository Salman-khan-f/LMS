package com.example.demo.repository;

import com.example.demo.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByLoanId(Long loanId);

    List<Payment> findByStatus(Payment.PaymentStatus status);

    Payment findByPaymentReference(String paymentReference);

    @Query("SELECT p FROM Payment p WHERE p.loan.id = :loanId AND p.paymentDate >= :startDate AND p.paymentDate <= :endDate")
    List<Payment> findPaymentsByLoanAndDateRange(@Param("loanId") Long loanId,
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(p.paymentAmount) FROM Payment p WHERE p.loan.id = :loanId AND p.status = 'PROCESSED'")
    BigDecimal getTotalPaymentsByLoan(@Param("loanId") Long loanId);

    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' AND p.paymentDate <= :date")
    List<Payment> findPendingPaymentsDueBy(@Param("date") LocalDate date);

    @Query("SELECT SUM(p.paymentAmount) FROM Payment p WHERE p.status = 'PROCESSED' AND p.paymentDate >= :startDate AND p.paymentDate <= :endDate")
    BigDecimal getTotalPaymentsInDateRange(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);
}