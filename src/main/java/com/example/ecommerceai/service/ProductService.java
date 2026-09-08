package com.example.ecommerceai.service;

import com.example.ecommerceai.entity.Product;
import com.example.ecommerceai.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
    }

    public int checkInventory(Long id) {
        return getProduct(id).getStock();
    }

    public List<Product> findAlternatives(Long id) {
        Product product = getProduct(id);

        return productRepository
                .findByCategoryIgnoreCase(product.getCategory())
                .stream()
                .filter(p -> !p.getId().equals(id))
                .toList();
    }

    public List<Product> compareProducts(List<Long> ids) {
        return ids.stream()
                .map(this::getProduct)
                .toList();
    }

    public List<Product> searchProducts(String query) {
        if (query == null || query.isBlank()) {
            return getAllProducts();
        }

        return productRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        query,
                        query
                );
    }
}