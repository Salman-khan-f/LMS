package com.example.demo.controller;

import com.example.demo.entity.Loan;
import com.example.demo.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanRepository loanRepository;

    @GetMapping
    public ResponseEntity<List<Loan>> getAllLoans() {
        List<Loan> loans = loanRepository.findAll();
        return new ResponseEntity<>(loans, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Loan> getLoanById(@PathVariable Long id) {
        Optional<Loan> loan = loanRepository.findById(id);
        return loan.map(l -> new ResponseEntity<>(l, HttpStatus.OK))
                   .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/number/{loanNumber}")
    public ResponseEntity<Loan> getLoanByNumber(@PathVariable String loanNumber) {
        Loan loan = loanRepository.findByLoanNumber(loanNumber);
        return loan != null ? new ResponseEntity<>(loan, HttpStatus.OK)
                           : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Loan>> getLoansByCustomer(@PathVariable Long customerId) {
        List<Loan> loans = loanRepository.findByLoanApplicationCustomerId(customerId);
        return new ResponseEntity<>(loans, HttpStatus.OK);
    }

    @GetMapping("/active")
    public ResponseEntity<List<Loan>> getActiveLoans() {
        List<Loan> loans = loanRepository.findActiveLoansWithOutstandingBalance();
        return new ResponseEntity<>(loans, HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Loan>> getLoansByStatus(@PathVariable String status) {
        try {
            Loan.LoanStatus loanStatus = Loan.LoanStatus.valueOf(status.toUpperCase());
            List<Loan> loans = loanRepository.findByStatus(loanStatus);
            return new ResponseEntity<>(loans, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/dashboard/summary")
    public ResponseEntity<LoanSummaryDTO> getLoanSummary() {
        Long totalLoans = loanRepository.count();
        Long activeLoans = loanRepository.countByStatus(Loan.LoanStatus.ACTIVE);
        BigDecimal totalOutstanding = loanRepository.getTotalOutstandingBalance();

        LoanSummaryDTO summary = new LoanSummaryDTO(totalLoans, activeLoans, totalOutstanding);
        return new ResponseEntity<>(summary, HttpStatus.OK);
    }

    public static class LoanSummaryDTO {
        private Long totalLoans;
        private Long activeLoans;
        private BigDecimal totalOutstandingBalance;

        public LoanSummaryDTO(Long totalLoans, Long activeLoans, BigDecimal totalOutstandingBalance) {
            this.totalLoans = totalLoans;
            this.activeLoans = activeLoans;
            this.totalOutstandingBalance = totalOutstandingBalance;
        }

        // Getters
        public Long getTotalLoans() { return totalLoans; }
        public Long getActiveLoans() { return activeLoans; }
        public BigDecimal getTotalOutstandingBalance() { return totalOutstandingBalance; }
    }
}