package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Entity // it will create a table in the database with the name "customers" and map this class to that table
@Table(name = "customers")
@Data
@NoArgsConstructor 
@AllArgsConstructor
public class Customer {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number should be valid")
    private String phoneNumber;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Zip code is required")
    @Pattern(regexp = "^\\d{5}(?:[-\\s]\\d{4})?$", message = "Zip code should be valid")
    private String zipCode;

    @NotNull(message = "Annual income is required")
    @Min(value = 0, message = "Annual income must be positive")
    private Double annualIncome;

    @NotBlank(message = "Employment status is required")
    private String employmentStatus;

    @Min(value = 0, message = "Credit score must be non-negative")
    @Max(value = 850, message = "Credit score cannot exceed 850")
    private Integer creditScore;

    @Column(nullable = false)
    private LocalDate createdDate = LocalDate.now();

    @Column(nullable = false)
    private LocalDate updatedDate = LocalDate.now();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LoanApplication> loanApplications;

    @PreUpdate
    public void preUpdate() {
        this.updatedDate = LocalDate.now();
    }
}