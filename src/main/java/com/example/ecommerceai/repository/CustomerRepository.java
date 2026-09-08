package com.example.ecommerceai.repository;
import com.example.ecommerceai.entity.Customers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customers, Long> {

    Optional<Customers> findByEmailIgnoreCase(String email);
}