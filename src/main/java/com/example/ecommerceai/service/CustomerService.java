package com.example.ecommerceai.service;

import com.example.ecommerceai.entity.Customers;
import com.example.ecommerceai.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customers> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customers getCustomer(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Customer not found: " + id));
    }

    public Customers getCustomerByEmail(String email) {
        return customerRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Customer not found with email: " + email));
    }
}