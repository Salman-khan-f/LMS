package com.example.demo.controller;

import com.example.demo.entity.Payment;
import com.example.demo.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentRepository paymentRepository;

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        Optional<Payment> payment = paymentRepository.findById(id);
        return payment.map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/reference/{reference}")
    public ResponseEntity<Payment> getPaymentByReference(@PathVariable String reference) {
        Payment payment = paymentRepository.findByPaymentReference(reference);
        return payment != null ? new ResponseEntity<>(payment, HttpStatus.OK)
                              : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/loan/{loanId}")
    public ResponseEntity<List<Payment>> getPaymentsByLoan(@PathVariable Long loanId) {
        List<Payment> payments = paymentRepository.findByLoanId(loanId);
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Payment>> getPaymentsByStatus(@PathVariable String status) {
        try {
            Payment.PaymentStatus paymentStatus = Payment.PaymentStatus.valueOf(status.toUpperCase());
            List<Payment> payments = paymentRepository.findByStatus(paymentStatus);
            return new ResponseEntity<>(payments, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody Payment payment) {
        // Generate payment reference if not provided
        if (payment.getPaymentReference() == null || payment.getPaymentReference().isEmpty()) {
            payment.setPaymentReference("PAY" + System.currentTimeMillis());
        }

        Payment savedPayment = paymentRepository.save(payment);
        return new ResponseEntity<>(savedPayment, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Payment> updatePaymentStatus(@PathVariable Long id, @RequestBody Map<String, String> statusUpdate) {
        Optional<Payment> optionalPayment = paymentRepository.findById(id);
        if (optionalPayment.isPresent()) {
            Payment payment = optionalPayment.get();
            try {
                Payment.PaymentStatus newStatus = Payment.PaymentStatus.valueOf(statusUpdate.get("status").toUpperCase());
                payment.setStatus(newStatus);

                if (newStatus == Payment.PaymentStatus.PROCESSED) {
                    payment.setProcessedDate(java.time.LocalDateTime.now());
                }

                Payment updatedPayment = paymentRepository.save(payment);
                return new ResponseEntity<>(updatedPayment, HttpStatus.OK);
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/pending/due-by")
    public ResponseEntity<List<Payment>> getPendingPaymentsDueBy(@RequestParam String date) {
        try {
            LocalDate dueDate = LocalDate.parse(date);
            List<Payment> payments = paymentRepository.findPendingPaymentsDueBy(dueDate);
            return new ResponseEntity<>(payments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<PaymentSummaryDTO> getPaymentSummary(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            BigDecimal totalPayments = paymentRepository.getTotalPaymentsInDateRange(start, end);
            Long totalTransactions = paymentRepository.findAll().stream()
                    .filter(p -> p.getPaymentDate().isAfter(start.minusDays(1)) && p.getPaymentDate().isBefore(end.plusDays(1)))
                    .count();

            PaymentSummaryDTO summary = new PaymentSummaryDTO(totalPayments, totalTransactions);
            return new ResponseEntity<>(summary, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public static class PaymentSummaryDTO {
        private BigDecimal totalAmount;
        private Long totalTransactions;

        public PaymentSummaryDTO(BigDecimal totalAmount, Long totalTransactions) {
            this.totalAmount = totalAmount;
            this.totalTransactions = totalTransactions;
        }

        // Getters
        public BigDecimal getTotalAmount() { return totalAmount; }
        public Long getTotalTransactions() { return totalTransactions; }
    }
}