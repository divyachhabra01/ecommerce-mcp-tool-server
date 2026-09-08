package com.example.ecommerceai.mcp;
import com.example.ecommerceai.entity.CartItem;
import com.example.ecommerceai.service.CartService;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CartMcpTools {

    private final CartService cartService;

    public CartMcpTools(CartService cartService) {
        this.cartService = cartService;
    }

    @McpTool(
            name = "get_cart",
            description = "Get the current shopping cart and its items for a customer."
    )
    public List<CartItem> getCart(
            @McpToolParam(description = "The customer ID")
            Long customerId
    ) {
        return cartService.getCartItems(customerId);
    }

    @McpTool(
            name = "add_to_cart",
            description = "Add a product to the customer's shopping cart."
    )
    public CartItem addToCart(
            @McpToolParam(description = "The customer ID")
            Long customerId,

            @McpToolParam(description = "The product ID")
            Long productId,

            @McpToolParam(description = "The quantity of the product to add")
            Integer quantity
    ) {
        return cartService.addToCart(customerId, productId, quantity);
    }

    @McpTool(
            name = "remove_from_cart",
            description = "Remove a product from the customer's shopping cart."
    )
    public String removeFromCart(
            @McpToolParam(description = "The customer ID")
            Long customerId,

            @McpToolParam(description = "The product ID to remove")
            Long productId
    ) {
        cartService.removeFromCart(customerId, productId);
        return "Product removed from cart successfully.";
    }

    @McpTool(
            name = "clear_cart",
            description = "Remove all products from the customer's shopping cart."
    )
    public String clearCart(
            @McpToolParam(description = "The customer ID")
            Long customerId
    ) {
        cartService.clearCart(customerId);
        return "Cart cleared successfully.";
    }
}