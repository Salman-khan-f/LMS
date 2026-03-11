package com.example.demo.config;

import com.example.demo.entity.Customer;
import com.example.demo.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) throws Exception {
        if (customerRepository.count() == 0) {
            initializeSampleData();
        }
    }

    private void initializeSampleData() {
        // Create sample customers
        Customer customer1 = new Customer();
        customer1.setFirstName("John");
        customer1.setLastName("Doe");
        customer1.setEmail("john.doe@example.com");
        customer1.setPhoneNumber("+1234567890");
        customer1.setDateOfBirth(LocalDate.of(1985, 5, 15));
        customer1.setAddress("123 Main St");
        customer1.setCity("New York");
        customer1.setState("NY");
        customer1.setZipCode("10001");
        customer1.setAnnualIncome(75000.0);
        customer1.setEmploymentStatus("FULL_TIME");
        customer1.setCreditScore(720);

        Customer customer2 = new Customer();
        customer2.setFirstName("Jane");
        customer2.setLastName("Smith");
        customer2.setEmail("jane.smith@example.com");
        customer2.setPhoneNumber("+1234567891");
        customer2.setDateOfBirth(LocalDate.of(1990, 8, 22));
        customer2.setAddress("456 Oak Ave");
        customer2.setCity("Los Angeles");
        customer2.setState("CA");
        customer2.setZipCode("90210");
        customer2.setAnnualIncome(95000.0);
        customer2.setEmploymentStatus("FULL_TIME");
        customer2.setCreditScore(780);

        Customer customer3 = new Customer();
        customer3.setFirstName("Bob");
        customer3.setLastName("Johnson");
        customer3.setEmail("bob.johnson@example.com");
        customer3.setPhoneNumber("+1234567892");
        customer3.setDateOfBirth(LocalDate.of(1975, 12, 10));
        customer3.setAddress("789 Pine Rd");
        customer3.setCity("Chicago");
        customer3.setState("IL");
        customer3.setZipCode("60601");
        customer3.setAnnualIncome(65000.0);
        customer3.setEmploymentStatus("PART_TIME");
        customer3.setCreditScore(650);

        customerRepository.save(customer1);
        customerRepository.save(customer2);
        customerRepository.save(customer3);

        System.out.println("Sample data initialized successfully!");
    }
}