package com.example.ecommerceai.mcp;

import com.example.ecommerceai.entity.Product;
import com.example.ecommerceai.service.ProductService;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMcpTools {

    private final ProductService productService;

    public ProductMcpTools(ProductService productService) {
        this.productService = productService;
    }

    @McpTool(
            name = "search_products",
            description = "Search products in the e-commerce catalog using a keyword, product name, or description."
    )
    public List<Product> searchProducts(
            @McpToolParam(
                    description = "Keyword or product description to search for"
            )
            String query
    ) {
        return productService.searchProducts(query);
    }

    @McpTool(
            name = "get_product",
            description = "Get complete details of a product using its product ID."
    )
    public Product getProduct(
            @McpToolParam(
                    description = "The unique product ID"
            )
            Long id
    ) {
        return productService.getProduct(id);
    }

    @McpTool(
            name = "check_inventory",
            description = "Check the current inventory or stock quantity of a product."
    )
    public String checkInventory(
            @McpToolParam(
                    description = "The unique product ID"
            )
            Long id
    ) {
        int stock = productService.checkInventory(id);

        if (stock <= 0) {
            return "Product is out of stock.";
        }

        return "Product is in stock. Available quantity: " + stock;
    }

    @McpTool(
            name = "find_alternatives",
            description = "Find alternative products in the same category as a specified product."
    )
    public List<Product> findAlternatives(
            @McpToolParam(
                    description = "The product ID for which alternatives should be found"
            )
            Long id
    ) {
        return productService.findAlternatives(id);
    }

    @McpTool(
            name = "compare_products",
            description = "Compare multiple products using their product IDs."
    )
    public List<Product> compareProducts(
            @McpToolParam(
                    description = "List of product IDs to compare"
            )
            List<Long> ids
    ) {
        return productService.compareProducts(ids);
    }
}