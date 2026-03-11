package com.example.demo.repository;

import com.example.demo.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    List<Customer> findByLastNameContainingIgnoreCase(String lastName);

    @Query("SELECT c FROM Customer c WHERE c.annualIncome >= :minIncome AND c.annualIncome <= :maxIncome")
    List<Customer> findByIncomeRange(@Param("minIncome") Double minIncome, @Param("maxIncome") Double maxIncome);

    @Query("SELECT c FROM Customer c WHERE c.creditScore >= :minScore")
    List<Customer> findByMinimumCreditScore(@Param("minScore") Integer minScore);

    @Query("SELECT COUNT(c) FROM Customer c WHERE c.createdDate >= :startDate AND c.createdDate <= :endDate")
    Long countCustomersCreatedBetween(@Param("startDate") java.time.LocalDate startDate,
                                    @Param("endDate") java.time.LocalDate endDate);
}