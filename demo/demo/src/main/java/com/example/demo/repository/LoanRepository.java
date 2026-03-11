package com.example.demo.repository;

import com.example.demo.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;


@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByLoanApplicationId(Long loanApplicationId);

    List<Loan> findByStatus(Loan.LoanStatus status);

    Loan findByLoanNumber(String loanNumber);

    List<Loan> findByLoanApplicationCustomerId(Long customerId);

    @Query("SELECT l FROM Loan l WHERE l.outstandingBalance > 0 AND l.status = 'ACTIVE'")
    List<Loan> findActiveLoansWithOutstandingBalance();

    @Query("SELECT l FROM Loan l WHERE l.endDate <= :date AND l.status = 'ACTIVE'")
    List<Loan> findLoansDueByDate(@Param("date") java.time.LocalDate date);

    @Query("SELECT SUM(l.outstandingBalance) FROM Loan l WHERE l.status = 'ACTIVE'")
    BigDecimal getTotalOutstandingBalance();

    @Query("SELECT COUNT(l) FROM Loan l WHERE l.status = :status")
    Long countByStatus(@Param("status") Loan.LoanStatus status);
    
}