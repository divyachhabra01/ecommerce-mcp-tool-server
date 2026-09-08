package com.example.ecommerceai.dto;

import com.example.ecommerceai.entity.Order;
import com.example.ecommerceai.entity.OrderItem;

import java.util.List;

public record OrderDetailsResponse(Order order, List<OrderItem> items) {
}