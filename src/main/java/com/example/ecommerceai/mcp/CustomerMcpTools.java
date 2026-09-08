package com.example.ecommerceai.mcp;

import com.example.ecommerceai.entity.Customers;
import com.example.ecommerceai.service.CustomerService;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomerMcpTools {

    private final CustomerService customerService;

    public CustomerMcpTools(CustomerService customerService) {
        this.customerService = customerService;
    }

    @McpTool(
            name = "get_customers",
            description = "Get all customers registered in the e-commerce application."
    )
    public List<Customers> getCustomers() {
        return customerService.getAllCustomers();
    }

    @McpTool(
            name = "get_customer",
            description = "Get customer details using the customer ID."
    )
    public Customers getCustomer(
            @McpToolParam(description = "The unique customer ID")
            Long id
    ) {
        return customerService.getCustomer(id);
    }

    @McpTool(
            name = "find_customer_by_email",
            description = "Find a customer using their email address."
    )
    public Customers findCustomerByEmail(
            @McpToolParam(description = "The customer's email address")
            String email
    ) {
        return customerService.getCustomerByEmail(email);
    }
}