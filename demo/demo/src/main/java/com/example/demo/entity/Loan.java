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
@Table(name = "loans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_application_id", nullable = false)
    private LoanApplication loanApplication;

    @NotBlank(message = "Loan number is required")
    @Column(unique = true)
    private String loanNumber;

    @NotNull(message = "Principal amount is required")
    @DecimalMin(value = "0.01", message = "Principal amount must be positive")
    private BigDecimal principalAmount;

    @NotNull(message = "Outstanding balance is required")
    @DecimalMin(value = "0.00", message = "Outstanding balance cannot be negative")
    private BigDecimal outstandingBalance;

    @NotNull(message = "Interest rate is required")
    @DecimalMin(value = "0.01", message = "Interest rate must be positive")
    private BigDecimal interestRate;

    @NotNull(message = "Loan term is required")
    @Min(value = 1, message = "Loan term must be at least 1 month")
    private Integer termMonths;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status = LoanStatus.ACTIVE;

    @Column(nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    private LocalDateTime closedDate;

    @DecimalMin(value = "0.01", message = "Monthly payment must be positive")
    private BigDecimal monthlyPayment;

    @Min(value = 0, message = "Late payment count cannot be negative")
    private Integer latePaymentCount = 0;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Payment> payments;

    public enum LoanStatus {
        ACTIVE,
        PAID_OFF,
        DEFAULTED,
        REFINANCED,
        WRITTEN_OFF
    }

    @PrePersist
    public void prePersist() {
        if (this.createdDate == null) {
            this.createdDate = LocalDateTime.now();
        }
        if (this.outstandingBalance == null) {
            this.outstandingBalance = this.principalAmount;
        }
    }
}