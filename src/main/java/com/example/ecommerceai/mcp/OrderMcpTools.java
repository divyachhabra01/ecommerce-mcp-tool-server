package com.example.ecommerceai.mcp;
import com.example.ecommerceai.dto.OrderDetailsResponse;
import com.example.ecommerceai.entity.Order;
import com.example.ecommerceai.service.OrderService;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class OrderMcpTools {

    private final OrderService orderService;

    public OrderMcpTools(OrderService orderService) {
        this.orderService = orderService;
    }

    @McpTool(
            name = "place_order",
            description = "Place a new order for a customer using their current cart."
    )
    public Order placeOrder(
            @McpToolParam(description = "The customer ID")
            Long customerId
    ) {
        return orderService.placeOrder(customerId);
    }

    @McpTool(
            name = "get_orders",
            description = "Get all orders for a customer."
    )
    public List<Order> getOrders(
            @McpToolParam(description = "The customer ID")
            Long customerId
    ) {
        return orderService.getOrders(customerId);
    }

    @McpTool(
            name = "get_order_details",
            description = "Get complete details of an order including its items."
    )
    public OrderDetailsResponse getOrderDetails(
            @McpToolParam(description = "The order ID")
            Long orderId
    ) {
        return orderService.getOrderWithItems(orderId);
    }

    @McpTool(
            name = "check_cancellation_eligibility",
            description = "Check whether an order can be cancelled."
    )
    public String checkCancellationEligibility(
            @McpToolParam(description = "The order ID")
            Long orderId
    ) {
        boolean eligible =
                orderService.checkCancellationEligibility(orderId);

        if (eligible) {
            return "Order is eligible for cancellation.";
        }

        return "Order is not eligible for cancellation.";
    }

    @McpTool(
            name = "cancel_order",
            description = "Cancel an eligible customer order."
    )
    public String cancelOrder(
            @McpToolParam(description = "The order ID")
            Long orderId
    ) {
        return orderService.cancelOrder(orderId);
    }
}