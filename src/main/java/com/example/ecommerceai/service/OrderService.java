package com.example.ecommerceai.service;

import com.example.ecommerceai.dto.OrderDetailsResponse;
import com.example.ecommerceai.entity.Cart;
import com.example.ecommerceai.entity.CartItem;
import com.example.ecommerceai.entity.Order;
import com.example.ecommerceai.entity.OrderItem;
import com.example.ecommerceai.entity.Product;
import com.example.ecommerceai.repository.OrderItemRepository;
import com.example.ecommerceai.repository.OrderRepository;
import com.example.ecommerceai.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            ProductRepository productRepository,
            CartService cartService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
    }

    public Order placeOrder(Long customerId) {

        Cart cart = cartService.getCart(customerId);
        List<CartItem> items = cartService.getCartItems(customerId);

        if (items.isEmpty()) {
            throw new RuntimeException("Cart is empty. Add products before placing an order.");
        }

        for (CartItem item : items) {
            Product product = item.getProduct();
            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException(
                        "Insufficient stock for product: " + product.getName());
            }
        }

        Order order = new Order();
        order.setCustomerId(customerId);
        order.setTotal(cart.getTotal());
        order.setStatus("PLACED");

        Order savedOrder = orderRepository.save(order);

        for (CartItem item : items) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(savedOrder.getId());
            orderItem.setProductId(item.getProduct().getId());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(item.getProduct().getPrice());
            orderItemRepository.save(orderItem);

            Product product = item.getProduct();
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }

        cartService.clearCart(customerId);

        return savedOrder;
    }

    @Transactional(readOnly = true)
    public List<Order> getOrders(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    @Transactional(readOnly = true)
    public Order getOrderDetails(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found: " + orderId));
    }

    @Transactional(readOnly = true)
    public OrderDetailsResponse getOrderWithItems(Long orderId) {
        Order order = getOrderDetails(orderId);
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        return new OrderDetailsResponse(order, items);
    }

    @Transactional(readOnly = true)
    public boolean checkCancellationEligibility(Long orderId) {
        Order order = getOrderDetails(orderId);

        return "PLACED".equalsIgnoreCase(order.getStatus());
    }

    public String cancelOrder(Long orderId) {
        Order order = getOrderDetails(orderId);

        if (!"PLACED".equalsIgnoreCase(order.getStatus())) {
            return "Order cannot be cancelled. Current status: "
                    + order.getStatus();
        }

        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        for (OrderItem item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() ->
                            new RuntimeException("Product not found: " + item.getProductId()));
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus("CANCELLED");
        orderRepository.save(order);

        return "Order cancelled successfully.";
    }
}