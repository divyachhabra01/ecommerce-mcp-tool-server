package com.example.ecommerceai.service;

import com.example.ecommerceai.entity.Cart;
import com.example.ecommerceai.entity.CartItem;
import com.example.ecommerceai.entity.Product;
import com.example.ecommerceai.repository.CartItemRepository;
import com.example.ecommerceai.repository.CartRepository;
import com.example.ecommerceai.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    public Cart getCart(Long customerId) {
        return cartRepository.findByCustomerId(customerId)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setCustomerId(customerId);
                    cart.setTotal(BigDecimal.ZERO);
                    return cartRepository.save(cart);
                });
    }

    public List<CartItem> getCartItems(Long customerId) {
        Cart cart = getCart(customerId);
        return cartItemRepository.findByCartId(cart.getId());
    }

    public CartItem addToCart(Long customerId, Long productId, Integer quantity) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        if (quantity == null || quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than zero.");
        }

        Cart cart = getCart(customerId);

        CartItem item = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProduct(product);
                    newItem.setQuantity(0);
                    return newItem;
                });

        int newTotalQuantity = item.getQuantity() + quantity;

        if (product.getStock() < newTotalQuantity) {
            throw new RuntimeException("Insufficient stock.");
        }

        item.setQuantity(newTotalQuantity);

        CartItem savedItem = cartItemRepository.save(item);

        updateCartTotal(cart);

        return savedItem;
    }

    public void removeFromCart(Long customerId, Long productId) {

        Cart cart = getCart(customerId);

        CartItem item = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new RuntimeException("Product not found in cart."));

        cartItemRepository.delete(item);

        updateCartTotal(cart);
    }

    public void clearCart(Long customerId) {

        Cart cart = getCart(customerId);

        cartItemRepository.deleteByCartId(cart.getId());

        cart.setTotal(BigDecimal.ZERO);
        cartRepository.save(cart);
    }

    private void updateCartTotal(Cart cart) {

        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());

        BigDecimal total = items.stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotal(total);
        cartRepository.save(cart);
    }
}